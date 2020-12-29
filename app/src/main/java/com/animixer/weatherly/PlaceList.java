package com.animixer.weatherly;

class PlaceList {

    private String mNameData;
    private String mStateData;
    private String mCountryData;

    public PlaceList(String mNameData, String mStateData, String mCountryData) {
        this.mNameData = mNameData;
        this.mStateData = mStateData;
        this.mCountryData = mCountryData;
    }

    public String getmNameData() {
        return mNameData;
    }

    public String getmStateData() {
        return mStateData;
    }

    public String getmCountryData() {
        return mCountryData;
    }

}
