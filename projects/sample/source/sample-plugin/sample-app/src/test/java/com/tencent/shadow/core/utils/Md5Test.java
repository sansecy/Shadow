package com.tencent.shadow.core.utils;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

import javax.xml.parsers.ParserConfigurationException;

import groovy.util.Node;
import groovy.util.NodeList;
import groovy.xml.Namespace;
import groovy.xml.XmlNodePrinter;
import groovy.xml.XmlParser;

public class Md5Test {

    @Test(expected = RuntimeException.class)
    public void nullAsFile() {

    }

    @Test
    public void emptyFile() throws IOException {
        try {

            String manifestPath = "/Users/sansecy/Shadow/projects/sample/source/sample-plugin/sample-app/build/intermediates/merged_manifest/pluginDebug/AndroidManifest.xml";
            String duplicateManifestPath = "/Users/sansecy/Shadow/projects/sample/source/sample-plugin/sample-base/build/intermediates/merged_manifests/pluginDebug/AndroidManifest.xml";
            removeDuplicateNode(manifestPath, duplicateManifestPath);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeDuplicateNode(String manifestPath, String duplicateManifestPath) throws ParserConfigurationException, SAXException, IOException {
        groovy.util.Node fromManifest = new XmlParser().parse(manifestPath);
        groovy.util.Node duplicateManifest = new XmlParser().parse(duplicateManifestPath);
        Namespace android = new Namespace("http://schemas.android.com/apk/res/android", "android");

        String s = fromManifest.toString();
//        System.out.println("manifestPath = " + s);
//        def nodesR = manifest.application[0].findAll {
//            //选择要删除的节点
//            (it.name() == "provider" && (it.attribute(android.name) == "com.dangbei.providertest.DBFileProvider"))
//        }
//        for (int i = 0; i < nodesR.size(); i++) {
//            Node parentNode = nodesR[i]
//            manifest.application[0].remove(parentNode)
//        }
        NodeList fromApplication = (NodeList) fromManifest.get("application");
        NodeList duplicateApplication = (NodeList) duplicateManifest.get("application");
        Node fromApplicationNode = (Node) fromApplication.get(0);
        Node duplicateApplicationNode = (Node) duplicateApplication.get(0);
        Iterator fromIterator = fromApplicationNode.iterator();
        Iterator duplicateIterator = duplicateApplicationNode.iterator();
        ArrayList<Node> nodesToRemove = new ArrayList<>();
        ArrayList<Node> fromNodes = new ArrayList<>();

        //收集源activity标签
        while (fromIterator.hasNext()) {
            Node node = (Node) fromIterator.next();
            fromNodes.add(node);
        }
        //遍历重复Activity标签，包含在from中，把重复的标签添加到nodesToRemove中
        while (duplicateIterator.hasNext()) {
            Node node = (Node) duplicateIterator.next();
//            String nodeName = (String) node.name();
            String duplicateNameAttribute = (String) node.attribute(android.get("name"));

            for (Node fromNode : fromNodes) {
                String fromNodeName = (String) fromNode.name();
                if (fromNodeName.equals("activity") ||
                        fromNodeName.equals("service") ||
                        fromNodeName.equals("receiver") ||
                        fromNodeName.equals("provider")
                ) {
                    String fromNameAttribute = (String) fromNode.attribute(android.get("name"));

                    if (fromNameAttribute.equals(duplicateNameAttribute)) {
                        nodesToRemove.add(fromNode);
                        System.out.println(fromNameAttribute);
                    }
                }
            }
        }
        for (Node node : nodesToRemove) {
            fromApplicationNode.remove(node);
        }
//        File file = new File(manifestPath);
//        File newXml = new File(file.getParentFile(), file.getName().replace(".xml", "-new.xml"));
        PrintWriter writer = new PrintWriter(manifestPath, "UTF8");
        new XmlNodePrinter(writer).print(fromManifest);
    }
    @Test
    public void smallFile() throws IOException {

    }
}
