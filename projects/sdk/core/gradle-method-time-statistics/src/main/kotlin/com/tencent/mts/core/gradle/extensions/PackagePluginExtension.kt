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

package com.tencent.mts.core.gradle.extensions

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

open class PackagePluginExtension {

    var loaderApkProjectPath = ""
    var runtimeApkProjectPath = ""

    var archivePrefix = ""
    var archiveSuffix = ""
    var destinationDir = ""

    var uuid = ""
    var version: Int = 0
    var uuidNickName = ""
    var compactVersion: Array<Int> = emptyArray()

    var buildTypes: NamedDomainObjectContainer<PluginBuildType>

    constructor(project: Project) {
        buildTypes = project.container(PluginBuildType::class.java)
        buildTypes.all {
            it.pluginApks = project.container(PluginApkConfig::class.java)
        }
    }

    fun pluginTypes(closure: Closure<PluginBuildType>) {
        buildTypes.configure(closure)
    }
}