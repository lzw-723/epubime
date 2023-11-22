package fun.lzwi.epubime.document.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fun.lzwi.epubime.util.ListUtils;

/*
 * https://www.w3.org/TR/epub-33/#sec-pkg-metadata
 */
public class MetaData implements Cloneable {
    // dc:
    private DC dc = new DC();

    private Meta meta = new Meta();

    /**
     * @return the dc
     */
    public DC getDc() {
        return dc.clone();
    }

    /**
     * @return the meta
     */
    public Meta getMeta() {
        return meta.clone();
    }

    /**
     * Meta
     */
    public static class Meta implements Cloneable {
        // meta
        // https://idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#Section2.2
        // https://www.w3.org/TR/epub-33/#sec-meta-elem
        Map<String, String> items = new HashMap<>();

        public int size() {
            return items.size();
        }

        public void put(String name, String content) {
            items.put(name, content);
        }

        public String get(String name) {
            return items.get(name);
        }

        @Override
        protected Meta clone(){
            try {
                return (Meta) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    /**
     * DC
     */
    public static class DC implements Cloneable {
        // 1 and more
        List<String> identifiers = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> languages = new ArrayList<>();
        // 0 and more
        List<String> contributors = new ArrayList<>();
        List<String> coverages = new ArrayList<>();
        List<String> creators = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> formats = new ArrayList<>();
        List<String> publishers = new ArrayList<>();
        List<String> relations = new ArrayList<>();
        List<String> rights = new ArrayList<>();
        List<String> sources = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        List<String> types = new ArrayList<>();

        public List<String> getIdentifiers() {
            return ListUtils.copy(identifiers);
        }

        public void addIdentifier(String id) {
            identifiers.add(id);
        }

        public List<String> getTitles() {
            return ListUtils.copy(titles);
        }

        public void addTitle(String title) {
            titles.add(title);
        }

        public List<String> getLanguages() {
            return ListUtils.copy(languages);
        }

        public void addLanguage(String language) {
            languages.add(language);
        }

        public List<String> getContributors() {
            return ListUtils.copy(contributors);
        }

        public void addContributor(String contributor) {
            contributors.add(contributor);
        }

        public List<String> getCoverages() {
            return ListUtils.copy(coverages);
        }

        public void addCoverage(String coverage) {
            coverages.add(coverage);
        }

        public List<String> getCreators() {
            return ListUtils.copy(creators);
        }

        public void addCreator(String creator) {
            creators.add(creator);
        }

        public List<String> getDates() {
            return ListUtils.copy(dates);
        }

        public void addDate(String date) {
            dates.add(date);
        }

        public List<String> getDescriptions() {
            return ListUtils.copy(descriptions);
        }

        public void addDescription(String desc) {
            descriptions.add(desc);
        }

        public List<String> getFormats() {
            return ListUtils.copy(formats);
        }

        public void addFormat(String format) {
            formats.add(format);
        }

        public List<String> getPublishers() {
            return ListUtils.copy(publishers);
        }

        public void addPublisher(String publisher) {
            publishers.add(publisher);
        }

        public List<String> getRelations() {
            return ListUtils.copy(relations);
        }

        public void addRelation(String rel) {
            relations.add(rel);
        }

        public List<String> getRights() {
            return ListUtils.copy(rights);
        }

        public void addRight(String right) {
            rights.add(right);
        }

        public List<String> getSources() {
            return ListUtils.copy(sources);
        }

        public void addSource(String source) {
            sources.add(source);
        }

        public List<String> getSubjects() {
            return ListUtils.copy(subjects);
        }

        public void addSubject(String sub) {
            subjects.add(sub);
        }

        public List<String> getTypes() {
            return ListUtils.copy(types);
        }

        public void addType(String type) {
            types.add(type);
        }

        @Override
        protected DC clone() {
            try {
                return (DC) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public MetaData clone() {
        try {
            return (MetaData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // @Override
    // public String toString() {
    // return "MetaData [identifiers=" + identifiers + ", titles=" + titles + ",
    // languages=" + languages
    // + ", contributors=" + contributors + ", coverages=" + coverages + ",
    // creators=" + creators + ", dates="
    // + dates + ", descriptions=" + descriptions + ", formats=" + formats + ",
    // publishers=" + publishers
    // + ", relations=" + relations + ", rights=" + rights + ", sources=" + sources
    // + ", subjects=" + subjects
    // + ", types=" + types + "]";
    // }
}
