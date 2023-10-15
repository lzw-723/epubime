package fun.lzwi.epubime.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import fun.lzwi.Utils;
import fun.lzwi.epubime.bean.ManifestItem;
import fun.lzwi.epubime.bean.MetaDC;
import fun.lzwi.epubime.bean.MetaItem;
import fun.lzwi.epubime.bean.PackageInfo;
import fun.lzwi.epubime.bean.SpineItemRef;

public class OpfUtilsTest {
    // opf文件
    File FILE;
    InputStream in;

    @Before
    public void before() throws IOException {
        FILE = Utils.getFile("content.opf");
        in = Files.newInputStream(FILE.toPath());
    }

    @Test
    public void testGetIdentifier() {
        Node pkg = OpfUtils.getPackage(in);
        Node metaData = OpfUtils.getMetaData(pkg);
        List<MetaItem> metaDataItems = OpfUtils.getMetaDataItems(metaData);
        List<MetaDC> metaDCs = OpfUtils.getMetaDCs(metaDataItems);
        String identifier = OpfUtils.getIdentifier(metaDCs);
        assertEquals("正确读取Id", "urn:uuid:d249e6e6-810d-43bb-91a4-9b8533daebe0", identifier);
    }

    @Test
    public void testGetLanguage() {
        Node pkg = OpfUtils.getPackage(in);
        Node metaData = OpfUtils.getMetaData(pkg);
        List<MetaItem> metaDataItems = OpfUtils.getMetaDataItems(metaData);
        List<MetaDC> metaDCs = OpfUtils.getMetaDCs(metaDataItems);
        String language = OpfUtils.getLanguage(metaDCs);
        assertEquals("正确读取language", "en", language);
    }

    @Test
    public void testGetTitle() {
        Node pkg = OpfUtils.getPackage(in);
        Node metaData = OpfUtils.getMetaData(pkg);
        List<MetaItem> metaDataItems = OpfUtils.getMetaDataItems(metaData);
        List<MetaDC> metaDCs = OpfUtils.getMetaDCs(metaDataItems);
        String title = OpfUtils.getTitle(metaDCs);
        assertEquals("正确读取title", "[此处填写主标题]", title);
    }

    @Test
    public void testGetManifest() {
        Node pkg = OpfUtils.getPackage(in);
        Node manifest = OpfUtils.getManifest(pkg);
        assertEquals("manifest子节点数目正确", 3, XmlUtils.countChildNodes(manifest.getChildNodes()));
    }

    @Test
    public void testGetSpine() {
        Node pkg = OpfUtils.getPackage(in);
        Node spine = OpfUtils.getSpine(pkg);
        assertEquals("spine子节点数目正确", 2, XmlUtils.countChildNodes(spine.getChildNodes()));
    }

    @Test
    public void testGetManifestItems() {
        Node pkg = OpfUtils.getPackage(in);
        Node manifest = OpfUtils.getManifest(pkg);
        List<ManifestItem> manifestItems = OpfUtils.getManifestItems(manifest);
        assertEquals("正确获取manifest的item", 3, manifestItems.size());
    }

    @Test
    public void testGetSpineItemRefs() {
        Node pkg = OpfUtils.getPackage(in);
        Node spine = OpfUtils.getSpine(pkg);
        List<SpineItemRef> spineItemRefs = OpfUtils.getSpineItemRefs(spine);
        assertEquals("正确获取spine的itemref", 2, spineItemRefs.size());
    }

    @Test
    public void testGetMetaData() {
        Node pkg = OpfUtils.getPackage(in);
        Node metaData = OpfUtils.getMetaData(pkg);
        assertEquals("metadata子节点数目正确", 5, XmlUtils.countChildNodes(metaData.getChildNodes()));
    }

    @Test
    public void testGetMetaDataItems() {
        Node pkg = OpfUtils.getPackage(in);
        Node metaData = OpfUtils.getMetaData(pkg);
        List<MetaItem> metaDataItems = OpfUtils.getMetaDataItems(metaData);
        assertEquals("正确获取metadata的meta", 5, metaDataItems.size());
    }

    @Test
    public void testGetMetaDCs() {
        Node pkg = OpfUtils.getPackage(in);
        Node metaData = OpfUtils.getMetaData(pkg);
        List<MetaItem> metaDataItems = OpfUtils.getMetaDataItems(metaData);
        List<MetaDC> metaDCs = OpfUtils.getMetaDCs(metaDataItems);
        assertEquals("正确获取metadata的dc", 3, metaDCs.size());
    }

    @Test
    public void testGetPackageInfo() {
        Node pkg = OpfUtils.getPackage(in);
        PackageInfo packageInfo = OpfUtils.getPackageInfo(pkg);
        assertNotNull("能获取Package信息", packageInfo);
    }
}
