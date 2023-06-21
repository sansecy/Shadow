package cn.migu.gamehall.shadow.core.manifest_parser

import cn.migu.gamehall.shadow.core.manifest_parser.AndroidManifestKeys
import cn.migu.gamehall.shadow.core.manifest_parser.AndroidManifestReader
import org.junit.Assert
import org.junit.Test
import java.io.File

class AndroidManifestReaderTest {
    @Test
    fun testReadXml() {
        val testFile = File(javaClass.classLoader.getResource("sample-app.xml")!!.toURI())
        val androidManifest = AndroidManifestReader().read(testFile)
        Assert.assertEquals(
            "cn.migu.gamehall.shadow.sample.host",
            androidManifest[AndroidManifestKeys.`package`]
        )
        Assert.assertEquals(
            "cn.migu.gamehall.shadow.sample.plugin.app.lib.UseCaseApplication",
            androidManifest[AndroidManifestKeys.name]
        )
        Assert.assertEquals(
            "cn.migu.gamehall.shadow.test.plugin.androidx_cases.lib.TestComponentFactory",
            androidManifest[AndroidManifestKeys.appComponentFactory]
        )
        Assert.assertEquals(
            "@ref/0x01030006",
            androidManifest[AndroidManifestKeys.theme]
        )
    }
}