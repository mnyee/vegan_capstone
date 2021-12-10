package com.caucap2021_1_2_10.ddubuk2;

import com.naver.maps.geometry.LatLng;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ted.gun0912.clustering.clustering.TedClusterItem;
import ted.gun0912.clustering.geometry.TedLatLng;

@Metadata(
        mv = {1, 1, 18},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005B+\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0007¢\u0006\u0002\u0010\tB\r\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\t\u0010\u0016\u001a\u00020\u000bHÆ\u0003J\u0013\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\n\u001a\u00020\u000bHÆ\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bHÖ\u0003J\b\u0010\u001c\u001a\u00020\u001dH\u0016J\t\u0010\u001e\u001a\u00020\u001fHÖ\u0001J\t\u0010 \u001a\u00020\u0007HÖ\u0001R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\fR\u001c\u0010\b\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0011\"\u0004\b\u0015\u0010\u0013¨\u0006!"},
        d2 = {"Lcom/caucap2021_1_2_10/ddubuk2/NaverItem;", "Lted/gun0912/clustering/clustering/TedClusterItem;", "lat", "", "lng", "(DD)V", "title", "", "snippet", "(DDLjava/lang/String;Ljava/lang/String;)V", "position", "Lcom/naver/maps/geometry/LatLng;", "(Lcom/naver/maps/geometry/LatLng;)V", "getPosition", "()Lcom/naver/maps/geometry/LatLng;", "setPosition", "getSnippet", "()Ljava/lang/String;", "setSnippet", "(Ljava/lang/String;)V", "getTitle", "setTitle", "component1", "copy", "equals", "", "other", "", "getTedLatLng", "Lted/gun0912/clustering/geometry/TedLatLng;", "hashCode", "", "toString", "app_debug"}
)
public final class NaverItem implements TedClusterItem {
    @Nullable
    private String title;
    @Nullable
    private String snippet;
    @NotNull
    private LatLng position;

    @NotNull
    public TedLatLng getTedLatLng() {
        return new TedLatLng(this.position.latitude, this.position.longitude);
    }

    @Nullable
    public final String getTitle() {
        return this.title;
    }

    public final void setTitle(@Nullable String var1) {
        this.title = var1;
    }

    @Nullable
    public final String getSnippet() {
        return this.snippet;
    }

    public final void setSnippet(@Nullable String var1) {
        this.snippet = var1;
    }

    @NotNull
    public final LatLng getPosition() {
        return this.position;
    }

    public final void setPosition(@NotNull LatLng var1) {
        Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
        this.position = var1;
    }

    public NaverItem(@NotNull LatLng position) {
        Intrinsics.checkParameterIsNotNull(position, "position");
        this.position = position;
    }

    public NaverItem(double lat, double lng) {
        this(new LatLng(lat, lng));
        this.title = (String)null;
        this.snippet = (String)null;
    }

    public NaverItem(double lat, double lng, @Nullable String title, @Nullable String snippet) {
        this(new LatLng(lat, lng));
        this.title = title;
        this.snippet = snippet;
    }

    @NotNull
    public final LatLng component1() {
        return this.position;
    }

    @NotNull
    public final NaverItem copy(@NotNull LatLng position) {
        Intrinsics.checkParameterIsNotNull(position, "position");
        return new NaverItem(position);
    }

    // $FF: synthetic method
    public static NaverItem copy$default(NaverItem var0, LatLng var1, int var2, Object var3) {
        if ((var2 & 1) != 0) {
            var1 = var0.position;
        }

        return var0.copy(var1);
    }

    @NotNull
    public String toString() {
        return "NaverItem(position=" + this.position + ")";
    }

    public int hashCode() {
        LatLng var10000 = this.position;
        return var10000 != null ? var10000.hashCode() : 0;
    }

    public boolean equals(@Nullable Object var1) {
        if (this != var1) {
            if (var1 instanceof NaverItem) {
                NaverItem var2 = (NaverItem)var1;
                if (Intrinsics.areEqual(this.position, var2.position)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }
}
