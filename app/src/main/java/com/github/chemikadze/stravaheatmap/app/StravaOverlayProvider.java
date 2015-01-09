package com.github.chemikadze.stravaheatmap.app;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

final class StravaOverlayProvider extends UrlTileProvider {

    enum ActivityType { CYCLING, RUNNING, BOTH };
    enum Color { RED, YELLOW, BLUE };

    private final ActivityType type;
    private final Color color;

    public StravaOverlayProvider(ActivityType type, Color color) {
        super(256, 256);
        this.type = type;
        this.color = color;
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        String type = this.type.toString().toLowerCase();
        int color = this.color.ordinal() + 1;
        int normX = x, e = 1 << zoom;
        if (0 > y || y >= e) {
            throw new IllegalArgumentException();
        }
        while (normX < 0) {
            normX += e;
        }
        if (normX >= e) {
            normX %= e;
        }
        String s = String.format(
                "http://d2z9m7k9h4f0yp.cloudfront.net/tiles/%s/color%s/%s/%s/%s.png?v=3",
                type, color, zoom, normX, y);
        try {
            return new URL(s);
        } catch (MalformedURLException exc) {
            throw new AssertionError(exc);
        }
    }
}
