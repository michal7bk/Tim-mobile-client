package pl.michal.tim_client.utils.database;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    private FeedReaderContract() {
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "picture";
        public static final String COLUMN_NAME_TITLE = "userId";
        public static final String COLUMN_NAME_SUBTITLE = "picture";
    }
}
