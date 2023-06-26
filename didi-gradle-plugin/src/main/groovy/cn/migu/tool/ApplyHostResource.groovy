package cn.migu.tool

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.tasks.MergeResources
import com.android.sdklib.BuildToolInfo
import com.android.utils.StringHelper
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.internal.GFileUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

class ApplyHostResource implements Plugin<Project> {
    Project project;

    void apply(Project project) {
        this.project = project
        if (!project.plugins.hasPlugin("com.android.application")) {
            return
        }
        BaseAppModuleExtension android = project.extensions.getByName("android")
        project.afterEvaluate {
            project.logger.info "ApplyHostResource#project.afterEvaluate"
            android.applicationVariants.all { ApplicationVariantImpl variant ->
                def preBuildTaskName = StringHelper.appendCapitalized("pre", variant.name.capitalize() + "Build")
                MergeResources mergeResourcesTask = project.tasks.getByName("merge${variant.name.capitalize()}Resources")
                def preBuildTaskNameTask = project.tasks.getByName(preBuildTaskName)
                project.logger.info "ApplyHostResource#project.afterEvaluate ${variant.name}"
                preBuildTaskNameTask.doFirst {
//                    File publicFile = mergeResourcesTask.getPublicFile().isPresent() ? mergeResourcesTask.getPublicFile().get().getAsFile() : null;
                    File destinationDir = mergeResourcesTask.getOutputDir().get().getAsFile();
//                    println "publicFile = " + publicFile
                    println "destinationDir = " + destinationDir
                    File publicTxtFile = project.rootProject.file('build/public.txt')
                    if (!publicTxtFile.exists()) {
                        throw new RuntimeException("请先构建宿主生成public.txt")
                    }
                    println("public.txt in ${publicTxtFile.path}")
//                    File publicXMLFile = new File("${project.projectDir.path}/src/main/res/values/public.xml")
                    File publicXMLFile = new File("${project.buildDir.path}/intermediates/base/xml/values/public.xml")
                    convertPublicTxtToPublicXml(publicTxtFile, publicXMLFile, false)
                    println("set public.xml into ${publicXMLFile.path}")

                    def buildToolInfo = variant.variantData.globalScope.versionedSdkLoader.get().buildToolInfoProvider.get()
                    String aaptPath = buildToolInfo.getPath(BuildToolInfo.PathId.AAPT2)
                    compileXmlForAapt2(aaptPath, publicXMLFile, destinationDir)
                    project.logger.info "ApplyHostResource#project.afterEvaluate processResourcesTask.doFirst"
                }
            }
        }
    }
    /**
     * 转换publicTxt为publicXml
     * copy tinker:com.tencent.tinker.build.gradle.task.TinkerResourceIdTask#convertPublicTxtToPublicXml
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    void convertPublicTxtToPublicXml(File publicTxtFile, File publicXmlFile, boolean withId) {
        if (publicTxtFile == null || publicXmlFile == null || !publicTxtFile.exists() || !publicTxtFile.isFile()) {
            throw new GradleException("publicTxtFile ${publicTxtFile} is not exist or not a file")
        }

        GFileUtils.deleteQuietly(publicXmlFile)
        GFileUtils.mkdirs(publicXmlFile.getParentFile())
        GFileUtils.touch(publicXmlFile)

        project.logger.info "convert publicTxtFile ${publicTxtFile} to publicXmlFile ${publicXmlFile}"

        publicXmlFile.append("<!-- AUTO-GENERATED FILE.  DO NOT MODIFY -->")
        publicXmlFile.append("\n")
        publicXmlFile.append("<resources>")
        publicXmlFile.append("\n")
        Pattern linePattern = Pattern.compile(".*?:(.*?)/(.*?)\\s+=\\s+(.*?)")

        publicTxtFile.eachLine { def line ->
            Matcher matcher = linePattern.matcher(line)
            if (matcher.matches() && matcher.groupCount() == 3) {
                String resType = matcher.group(1)
                String resName = matcher.group(2)
                if (resName.startsWith('$')) {
                    project.logger.info "ignore to public res ${resName} because it's a nested resource"
                } else if (resType.equalsIgnoreCase("styleable")) {
                    project.logger.info "ignore to public res ${resName} because it's a styleable resource"
                } else {
                    if (withId) {
                        publicXmlFile.append("\t<public type=\"${resType}\" name=\"${resName}\" id=\"${matcher.group(3)}\" />\n")
                    } else {
                        publicXmlFile.append("\t<public type=\"${resType}\" name=\"${resName}\" />\n")
                    }

                }
            }
        }
        publicXmlFile.append("</resources>")
    }


    /**
     * compile xml file to flat file
     */
    void compileXmlForAapt2(aapt2Path, File xmlFile, outputDir) {
        if (xmlFile == null || !xmlFile.exists()) {
            return
        }
        project.logger.error("tinker get aapt2 path ${aapt2Path}")
        execCommand("${aapt2Path} compile --legacy -o ${outputDir} ${xmlFile}")
    }

    String execCommand(command) {
        println "execCommand = [$command]"
        Runtime runtime = Runtime.getRuntime()
        Process p = runtime.exec(command)
        InputStream fis = p.getInputStream()
        InputStreamReader isr = new InputStreamReader(fis)
        BufferedReader br = new BufferedReader(isr)
        String line = null
        StringBuilder sb = new StringBuilder()
        while ((line = br.readLine()) != null) {
            sb.append(line)
        }
        br.close()
        isr.close()
        fis.close()
        def result = sb.toString()
        println "result = [$result]"
        return result
    }


}