package steerclear.wm.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import butterknife.ButterKnife;
import clojure.lang.Obj;
import icepick.Icepick;
import steerclear.wm.MainApp;
import steerclear.wm.util.Logg;

/**
 * Created by mbpeele on 2/23/16.
 */
public abstract class BaseFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        Map<Object, Class> map = getCastMap();
        for (Object object: map.keySet()) {
            Class clazz = map.get(object);
            Object cast = clazz.cast(context);
            String objectTypeName = clazz.getName();

            for (Field field: getClass().getDeclaredFields()) {
                String fieldTypeName = field.getType().getName();

                if (Objects.equals(fieldTypeName, objectTypeName)) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    try {
                        field.set(this, cast);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Map<Object, Class> map = getCastMap();
        for (Object object: map.keySet()) {
            Class clazz = map.get(object);
            String objectTypeName = clazz.getName();

            for (Field field: getClass().getDeclaredFields()) {
                String fieldTypeName = field.getType().getName();

                if (Objects.equals(fieldTypeName, objectTypeName)) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    try {
                        field.set(this, null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public Snackbar showSnackBar(View view, int stringRes, int dur) {
        return Snackbar.make(view, stringRes, dur);
    }

    protected abstract Map<Object, Class> getCastMap();

    protected abstract int getLayoutId();
}
