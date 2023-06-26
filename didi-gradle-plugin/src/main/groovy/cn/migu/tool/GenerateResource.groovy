package cn.migu.tool

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class GenerateResource implements Plugin<Project> {
    void apply(Project project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            return
        }
//        BaseAppModuleExtension android = project.extensions.getByName("android")
//        File publicTxtFile = project.rootProject.file('build/public.txt')
//        //aapt2添加--emit-ids参数生成
//        android.aaptOptions.additionalParameters("--emit-ids", "${publicTxtFile}")
    }

    private static void processAapt2Enable(Project project, aaptOptions) {
        project.logger.error "aapt2 is enabled"
        File publicTxtFile = project.rootProject.file('build/public.txt')
        //public文件存在，则应用，不存在则生成
        if (publicTxtFile.exists()) {
            project.logger.error "${publicTxtFile} exists, apply it."
            //aapt2添加--stable-ids参数应用
            aaptOptions.additionalParameters("--stable-ids", "${publicTxtFile}")
        } else {
            project.logger.error "${publicTxtFile} not exists, generate it."

        }
    }
}