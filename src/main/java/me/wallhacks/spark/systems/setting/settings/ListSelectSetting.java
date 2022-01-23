package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

import java.util.*;
import java.util.function.Predicate;

public class ListSelectSetting<N> extends Setting<Map<N,Boolean>> {

    public ListSelectSetting(String name, SettingsHolder holder, N[] list, N[] selected, Predicate<Map<N,Boolean>> visible, String settingCategory) {

        super(new HashMap<N,Boolean>(), name, holder,visible,settingCategory);


        for (int i = 0; i < list.length; i++) {
            getValue().put(list[i], Arrays.asList(selected).contains(list[i]));
        }

    }
    public ListSelectSetting(String name, SettingsHolder holder, N[] list, N[] selected, String settingCategory) {
        this(name,holder,list,selected,null,settingCategory);
    }

        public void setValueSelected(N[] selected) {
        for (N string : getValue().keySet()) {
            getValue().put(string, Arrays.asList(selected).contains(string));
        }


    }
    public boolean contains(N check){
        if(getValue().containsKey(check))
            return getValue().get(check);
        return false;
    }



    public void setValueState(String selected, boolean state) {
        for (N string : getValue().keySet()) {
            if(getValueIdString(string).equals(selected))
                getValue().put(string, state);
        }
    }
    public void setValueState(N selected, boolean state) {
        if(getValue().containsKey(selected))
            getValue().put(selected, state);
    }

    public ArrayList<N> getSelected() {
        ArrayList<N> sel = new ArrayList<N>();

        for (N key : getValue().keySet()) {
            if(getValue().get(key))
                sel.add(key);

        }

        return sel;
    }

    public Set<N> getValues() {
        return getValue().keySet();
    }

    public boolean isValueSelected(N value) {
        return getValue().get(value);
    }
    public boolean toggle(N value) {
        if(getValue().containsKey(value))
            return getValue().put(value, !getValue().get(value));
        return false;
    }

    public String getValueDisplayString(N _t){
        return _t.toString();
    }
    public String getValueIdString(N _t){
        return _t.toString();
    }

    @Override
    public boolean setValueString(String value) {
        String[] list = value.split(",");
        for (N t : getValues())
            setValueState(t, Arrays.asList(list).contains(getValueIdString(t)));
        return true;
    }

    @Override
    public String getValueString() {
        String list = "";

        for (N t : getSelected())
            list = list+getValueIdString(t)+",";


        return list;
    }

}