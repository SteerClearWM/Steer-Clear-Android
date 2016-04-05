package steerclear.wm.data.model;

/**
 * Created by mbpeele on 4/5/16.
 */
public class AutoCompleteItem {

    public CharSequence placeId;
    public CharSequence description;

    public AutoCompleteItem(CharSequence placeId, CharSequence description) {
        this.placeId = placeId;
        this.description = description;
    }

    @Override
    public String toString() {
        return description.toString();
    }
}