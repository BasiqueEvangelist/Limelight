package me.basiqueevangelist.limelight.impl.resource.wiki;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import me.basiqueevangelist.limelight.impl.builtin.WikiExtension;

import java.util.function.Consumer;

public record MkDocsWikiSource(String url, boolean includeParagraphs) implements WikiSource {
    public static final Endec<MkDocsWikiSource> ENDEC = StructEndecBuilder.of(
            Endec.STRING.fieldOf("url", MkDocsWikiSource::url),
            Endec.BOOLEAN.optionalFieldOf("include_paragraphs", MkDocsWikiSource::includeParagraphs, false),
            MkDocsWikiSource::new
    );

    @Override
    public String createSearchUrl(String searchText) {
        return url + "/search/search_index.json";
    }

    @Override
    public void gatherEntriesFromSearch(String queryBody, String searchText, Consumer<WikiExtension.EntryData> entryConsumer) {
        var json = JsonParser.parseString(queryBody).getAsJsonObject();
        var pages = json.getAsJsonArray("docs");
        var lowercaseSearchText = searchText.toLowerCase();

        for (JsonElement el : pages) {
            var page = el.getAsJsonObject();
            var location = page.get("location").getAsString();

            if (!includeParagraphs && !location.isEmpty() && !location.endsWith("/")) {
                continue;
            }

            var title = page.get("title").getAsString();

            if (title.toLowerCase().contains(lowercaseSearchText)) {
                entryConsumer.accept(new WikiExtension.EntryData(title, url + "/" + location));
            }
        }
    }

    @Override
    public WikiSourceType<?> id() {
        return WikiSourceType.MKDOCS;
    }
}
