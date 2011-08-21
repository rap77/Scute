package org.hdesktop.swingx.renderer;

import java.util.Locale;
import java.util.Map;

import org.hdesktop.swingx.plaf.UIManagerExt;
import org.hdesktop.swingx.util.Contract;

/**
 * A StringValue which looks up localized String representations for objects.
 */
public class LocalizableStringValue implements StringValue {

    private Map<Object, String> lookup;

    private Locale locale;

    /**
     * Instantiates a LocaleStringValue which looks up localized String
     * representations for items in the map using the JComponent defaultLocale.
     * 
     * @param lookup a map containing Entries of objects and a string key to
     *        look up its string representation in the UIManager
     */
    public LocalizableStringValue(Map<Object, String> lookup) {
        this(lookup, null);
    }

    /**
     * Instantiates a LocaleStringValue which looks up localized String
     * representations for items in the map using the given Locale.
     * 
     * @param lookup a map containing Entries of objects and a string key to
     *        look up its string representation in the UIManager
     * @param locale the locale to lookup the localized strings, may be null to
     *        denote using JComponent.defaultLocale
     */
    public LocalizableStringValue(Map<Object, String> lookup, Locale locale) {
        this.lookup = Contract.asNotNull(lookup, "map must not be null");
        setLocale(locale);
    }

    /**
     * 
     * @inherited <p>
     * 
     *            Implemented to lookup the value's localized string
     *            representation, if contained in the lookup map. Returns
     *            toString if not.
     * 
     */
    @Override
    public String getString(Object value) {
        String key = lookup.get(value);
        if (key != null) {
            String text = UIManagerExt.getString(key, getLocale());
            if (text != null)
                return text;
        }
        return StringValues.TO_STRING_UI.getString(value);
    }

    // -------------------- implement Localizable

    /**
     * Sets the Locale to use for lookup of localized string representation.
     * 
     * @param locale the locale to lookup the localized strings, may be null to
     *        denote using Locale's default.
     */
    public final void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Returns the Locale to use for lookup, guaranteed to be not null. If
     * the initial setting had been null, returns current Locale's default.
     * 
     * @return the Locale used for lookup.
     */
    public Locale getLocale() {
        return locale != null ? locale : Locale.getDefault();
    }
}