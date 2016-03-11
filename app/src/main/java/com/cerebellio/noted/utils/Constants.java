package com.cerebellio.noted.utils;

/**
 * Commonly used constant values
 */
public class Constants {

    private static final String LOG_TAG = TextFunctions.makeLogTag(Constants.class);

    public static final String BUNDLE_IS_IN_EDIT_MODE = "is_in_edit_mode";
    public static final String BUNDLE_ITEM_ID = "item_id";
    public static final String BUNDLE_ITEM_POSITION = "item_position";
    public static final String BUNDLE_ITEM_TYPE = "item_type";
    public static final String BUNDLE_ITEM_TO_EDIT_ID = "item_to_edit_id";
    public static final String BUNDLE_TAG_VALUE = "tag_value";

    public static final String BUNDLE_SETTINGS_XML = "settings_xml";
    public static final String BUNDLE_SETTINGS_TITLE = "settings_title";

    public static final String INTENT_ITEM_ID = "item_id";
    public static final String INTENT_REMINDER_ID = "reminder_id";
    public static final String INTENT_ITEM_TYPE = "type";
    public static final String INTENT_FROM_NOTIFICATION = "from_notification";

    //region Colours
    public static final Integer[] COLOURS = new Integer[]{
            0xFFE57373,
            0xFFF44336,
            0xFFC62828,

            0xFFF06292,
            0xFFE91E63,
            0xFFC2185B,

            0xFFBA68C8,
            0xFF9C27B0,
            0xFF7B1FA2,

            0xFF7986CB,
            0xFF3F51B5,
            0xFF303F9F,

            0xFF64B5F6,
            0xFF2196F3,
            0xFF1976D2,

            0xFF4DD0E1,
            0xFF00BCD4,
            0xFF0097A7,

            0xFF4DB6AC,
            0xFF009688,
            0xFF00796B,

            0xFF81C784,
            0xFF4CAF50,
            0xFF388E3C,

            0xFFFFF176,
            0xFFCDDC39,
            0xFFFBC02D,

            0xFFFFB74D,
            0xFFFF9800,
            0xFFF57C00,

            0xFFFF8A65,
            0xFFFF5722,
            0xFFE64A19,

            0xFFA1887F,
            0xFF795548,
            0xFF5D4037,

            0xFFE0E0E0,
            0xFF9E9E9E,
            0xFF616161,

            0xFF607D8B,
            0xFF455A64,
            0xFF000000
    };
    //endregion

    //region Material Colours
    public static final Integer[] MATERIAL_COLOURS = new Integer[]{
            0xFFF44336,
            0xFFE91E63,
            0xFF9C27B0,
            0xFF3F51B5,
            0xFF2196F3,
            0xFF03A9F4,
            0xFF00BCD4,
            0xFF009688,
            0xFF8BC34A,
            0xFFCDDC39,
            0xFFFFC107,
            0xFFFF9800,
            0xFFFF5722,
            0xFF795548,
            0xFF455A64,
    };
    //endregion

    private Constants() {}

}

