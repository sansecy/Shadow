/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.mts.core.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApplicationVariant
import com.tencent.mts.core.gradle.extensions.PackagePluginExtension
import com.tencent.mts.core.transform.ShadowTransform
import com.tencent.shadow.core.transform_kit.AndroidClassPoolBuilder
import com.tencent.shadow.core.transform_kit.ClassPoolBuilder
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

class ShadowPlugin : Plugin<Project> {

    private lateinit var androidClassPoolBuilder: ClassPoolBuilder
    private lateinit var contextClassLoader: ClassLoader
    private lateinit var agpCompat: AGPCompat

    override fun apply(project: Project) {
        agpCompat = buildAgpCompat(project)
        val baseExtension = try {
            project.extensions.getByName("android") as BaseExtension
        } catch (e: Exception) {
            return
        }
        //在这里取到的contextClassLoader包含运行时库(classpath方式引入的)shadow-runtime
        contextClassLoader = Thread.currentThread().contextClassLoader
        val lateInitBuilder = object : ClassPoolBuilder {
            override fun build() = androidClassPoolBuilder.build()
        }

        val shadowExtension = project.extensions.create("shadow", ShadowExtension::class.java)
        if (!project.hasProperty("disable_shadow_transform")) {
            baseExtension.registerTransform(ShadowTransform(
                project,
                lateInitBuilder,
                { shadowExtension.transformConfig.useHostContext }
            ))
        }

        project.extensions.create("packagePlugin", PackagePluginExtension::class.java, project)

        project.afterEvaluate {
            initAndroidClassPoolBuilder(baseExtension, project)

//            createPackagePluginTasks(project)

            onEachPluginVariant(project) { pluginVariant ->

                val appExtension: AppExtension =
                    project.extensions.getByType(AppExtension::class.java)
            }
        }

        checkKotlinAndroidPluginForPluginManifestTask(project)
    }

    /**
     * GeneratePluginManifestTask会向android DSL添加新的java源码目录，
     * 而kotlin-android会在syncKotlinAndAndroidSourceSets中接管java的源码目录，
     * 从而使后添加到android DSL中的java目录失效。
     */
    private fun checkKotlinAndroidPluginForPluginManifestTask(project: Project) {
        if (project.plugins.hasPlugin("kotlin-android")) {
            throw Error("必须在kotlin-android之前应用com.tencent.shadow.plugin")
        }
    }

    private fun createPackagePluginTasks(project: Project) {
        val packagePlugin = project.extensions.findByName("packagePlugin")
        val extension = packagePlugin as PackagePluginExtension
        val buildTypes = extension.buildTypes

        val tasks = mutableListOf<Task>()
        for (i in buildTypes) {
            project.logger.info("buildTypes = " + i.name)
            val task = createPackagePluginTask(project, i)
            tasks.add(task)
        }
        if (tasks.isNotEmpty()) {
            project.tasks.create("packageAllPlugin") {
                it.group = "plugin"
                it.description = "打包所有插件"
            }.dependsOn(tasks)
        }
    }

    private fun onEachPluginVariant(project: Project, actions: (ApplicationVariant) -> Unit) {
        val appExtension: AppExtension = project.extensions.getByType(AppExtension::class.java)
        val pluginVariants = appExtension.applicationVariants

        pluginVariants.forEach(actions)
    }

    private fun initAndroidClassPoolBuilder(
        baseExtension: BaseExtension,
        project: Project
    ) {
        val sdkDirectory = baseExtension.sdkDirectory
        val compileSdkVersion =
            baseExtension.compileSdkVersion ?: throw IllegalStateException("compileSdkVersion获取失败")
        val androidJarPath = "platforms/${compileSdkVersion}/android.jar"
        val androidJar = File(sdkDirectory, androidJarPath)

        androidClassPoolBuilder = AndroidClassPoolBuilder(project, contextClassLoader, androidJar)
    }

    open class ShadowExtension {
        var transformConfig = TransformConfig()
        fun transform(action: Action<in TransformConfig>) {
            action.execute(transformConfig)
        }
    }

    class TransformConfig {
        var useHostContext: Array<String> = emptyArray()
    }

    companion object {
        const val locateApkanalyzerTaskName = "locateApkanalyzer"
        private fun Project.locateApkanalyzerResultPath() =
            File(rootProject.buildDir, "shadow/ApkanalyzerPath.txt")

        private fun buildAgpCompat(project: Project): AGPCompat {
            return AGPCompatImpl()
        }
    }

}