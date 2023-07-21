package fun.lzwi.epubime;

import fun.lzwi.epubime.document.NavigationDocument;
import fun.lzwi.epubime.document.PackageDocument;

public class Epub {
    private PackageDocument packageDocument;
    private NavigationDocument navigationDocument;
    public PackageDocument getPackageDocument() {
        if (packageDocument == null) {
            return null;
        }
        return packageDocument.clone();
    }
    public void setPackageDocument(PackageDocument packageDocument) {
        this.packageDocument = packageDocument.clone();
    }
    public NavigationDocument getNavigationDocument() {
        if (navigationDocument == null) {
            return null;
        }
        return navigationDocument.clone();
    }
    public void setNavigationDocument(NavigationDocument navigationDocument) {
        this.navigationDocument = navigationDocument.clone();
    }
}
