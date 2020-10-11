package com.usenergysolutions.energybroker.service;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.usenergysolutions.energybroker.config.Constants;
import com.usenergysolutions.energybroker.services.communication.MyLocationTransmitterService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class MyLocationTransmitterIntentTest {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Test
    public void testWithStartedService() {
        try {
            double lat = 51.18139833333333;
            double lng = 4.601638333333334;
            long time = 1549021931345L;
            Intent intent = new Intent(InstrumentationRegistry.getTargetContext(),
                    MyLocationTransmitterService.class);
            intent.putExtra(Constants.NAME_LATITUDE_DATA, lat);
            intent.putExtra(Constants.NAME_LONGITUDE_DATA, lng);
            intent.putExtra(Constants.NAME_SAMPLE_TIME, time);
            mServiceRule.startService(intent);
        } catch (TimeoutException e) {
            Log.d("MyLocationTransmitterIntentTest", "testWithStartedService", e);
            e.printStackTrace();
        }
    }
}
