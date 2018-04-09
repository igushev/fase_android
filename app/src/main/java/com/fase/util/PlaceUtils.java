package com.fase.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.google.android.gms.location.places.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


public class PlaceUtils {

    public static com.fase.model.data.Place getPlace(Context context, Place place) {
        com.fase.model.data.Place placeInfo = null;

        if (place != null) {
            List<Address> addresses = getAddresses(context, place);

            String city = " ";
            String country = " ";
            String state = " ";

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                if (!TextUtils.isEmpty(address.getLocality())) {
                    city = address.getLocality();
                }
                if (!TextUtils.isEmpty(address.getAdminArea())) {
                    state = address.getAdminArea();
                }
                if (!TextUtils.isEmpty(address.getCountryName())) {
                    country = address.getCountryName();
                }
            }

            placeInfo = new com.fase.model.data.Place();
            placeInfo.setCity(city);
            placeInfo.setCountry(country);
            placeInfo.setState(state);
            placeInfo.setGooglePlaceId(place.getId());
        }

        return placeInfo;
    }

    private static List<Address> getAddresses(Context context, Place place) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            return geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
        } catch (IOException e) {
            Timber.e("Error getting place info", e);
        }
        return null;
    }
}
