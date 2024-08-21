package me.basiqueevangelist.limelight.impl.resource.wiki;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import me.basiqueevangelist.limelight.impl.builtin.WikiExtension;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public record MediaWikiSource(String url) implements WikiSource {
    public static final Endec<MediaWikiSource> ENDEC = StructEndecBuilder.of(
        Endec.STRING.fieldOf("url", MediaWikiSource::url),
        MediaWikiSource::new
    );

    @Override
    public String createSearchUrl(String searchText) {
        return url() + "?action=opensearch&format=json&formatversion=2&limit=10&search=" + URLEncoder.encode(searchText, StandardCharsets.UTF_8);
    }

    @Override
    public void gatherEntriesFromSearch(String queryBody, String searchText, Consumer<WikiExtension.EntryData> entryConsumer) {
        var json = JsonParser.parseString(queryBody).getAsJsonArray();

        JsonArray titles = json.get(1).getAsJsonArray();
        JsonArray urls = json.get(3).getAsJsonArray();

        for (int idx = 0; idx < titles.size(); idx++) {
            entryConsumer.accept(new WikiExtension.EntryData(titles.get(idx).getAsString(), urls.get(idx).getAsString()));
        }
    }

    @Override
    public WikiSourceType<?> id() {
        return WikiSourceType.MEDIA_WIKI;
    }
}
