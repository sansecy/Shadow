package cn.migu.tool

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.tasks.R8Task
import com.didi.virtualapk.VAExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AppMapping implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        def AppMappingExt = project.extensions.create("MappingExtension", MappingExtension.class)
        def android = project.extensions.getByType(BaseAppModuleExtension.class);
        project.afterEvaluate {
            android.applicationVariants.each { ApplicationVariantImpl applicationVariant ->
                if (applicationVariant.buildType.minifyEnabled) {
                    applicationVariant.variantData.globalScope
                    R8Task proguardTask = project.tasks["minify${applicationVariant.name.capitalize()}WithR8"]
//                    + "/build/outputs/mapping/pluginRelease/mapping.txt"
                    def mappingFile = new File(AppMappingExt.hostDir)
                    println("host mapping file : ${mappingFile.path}")
//                    if (!mappingFile.exists()) {
//                        throw new RuntimeException("mapping file: ${mappingFile.path} not exist")
//                    }
                    proguardTask.testedMappingFile.from(project.file(mappingFile.path))
                    proguardTask.doLast {
                        println("doLast testedMappingFile = ${proguardTask.testedMappingFile.getAsPath()}")
                    }
                }
            }
        }
    }
}
