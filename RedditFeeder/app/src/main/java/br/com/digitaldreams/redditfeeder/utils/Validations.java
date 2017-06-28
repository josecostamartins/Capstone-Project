package br.com.digitaldreams.redditfeeder.utils;

import android.text.TextUtils;

/**
 * Created by josecostamartins on 7/23/15.
 */
public class Validations {

    public final static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
