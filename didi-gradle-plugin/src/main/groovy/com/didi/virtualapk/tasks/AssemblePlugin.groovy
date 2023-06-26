package com.didi.virtualapk.tasks

import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.didi.virtualapk.VAExtension
import com.didi.virtualapk.utils.Log
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.sun.istack.NotNull

/**
 * Gradle task for assemble plugin apk
 * @author zhengtao
 */
class AssemblePlugin extends DefaultTask {

    @OutputDirectory
    File pluginApkDir

    @Input
    String appPackageName

    @Input
    String apkTimestamp

    @InputFile
    File originApkFile
    @Input
    String variantName
    @Input
    String buildDir

    /**
     * Copy the plugin apk to out/plugin directory and rename to
     * the format required for the backend system
     */
    @TaskAction
    void outputPluginApk() {
        println "AssemblePlugin.outputPluginApk "
        VAExtension virtualApk = project.virtualApk
        virtualApk.getVaContext().checkList.check()
        virtualApk.printWarning(name)

        if (virtualApk.getFlag('tip.forceUseHostDependences')) {
            def tip = new StringBuilder('To avoid configuration WARNINGs, you could set the forceUseHostDependences to be true in build.gradle,\n ')
            tip.append('please declare it in application project build.gradle:\n')
            tip.append('    virtualApk {\n')
            tip.append('        forceUseHostDependences = true \n')
            tip.append('    }\n')
            Log.i name, tip.toString()
        }

//        Log.i name, "More building infomation could be found in the dir: ${buildDir}"
//
        getProject().copy({
            from originApkFile
            into pluginApkDir
            rename { "${appPackageName}_${apkTimestamp}.apk" }
        })
    }


    static class ConfigAction implements Action<AssemblePlugin> {

        @NotNull
        Project project
        @NotNull
        ApplicationVariantImpl variant
        VAExtension virtualApk

        ConfigAction(@NotNull Project project, @NotNull ApkVariant variant) {
            this.project = project
            this.variant = variant
            this.virtualApk = project.virtualApk
        }

        @Override
        void execute(AssemblePlugin assemblePluginTask) {
            assemblePluginTask.appPackageName = variant.applicationId
            assemblePluginTask.apkTimestamp = new Date().format("yyyyMMddHHmm ss")
            assemblePluginTask.originApkFile = variant.outputs[0].outputFile
            assemblePluginTask.pluginApkDir = new File(project.buildDir, "/outputs/plugin/${variant.name}")
            assemblePluginTask.variantName = variant.name
            assemblePluginTask.buildDir = virtualApk.getVaContext().getBuildDir(project).canonicalPath
//
            assemblePluginTask.setGroup("build")
            assemblePluginTask.setDescription("Build ${variant.name.capitalize()} plugin apk")
            println "BasePlugin.apply variant.assemble.name=" + variant.assemble.name
            assemblePluginTask.dependsOn(variant.assemble.name)
        }
    }

}
