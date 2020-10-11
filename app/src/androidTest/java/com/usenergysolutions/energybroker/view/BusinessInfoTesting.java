package com.usenergysolutions.energybroker.view;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import com.usenergysolutions.energybroker.R;
import com.usenergysolutions.energybroker.model.ExtendedBusinessInfoModel;
import com.usenergysolutions.energybroker.model.UesPlaceModel;
import com.usenergysolutions.energybroker.model.converters.ToExtendedBusinessInfoModel;
import com.usenergysolutions.energybroker.model.ModelsFactory;
import com.usenergysolutions.energybroker.view.maps.BusinessInfoActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.is;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class BusinessInfoTesting {
    // Test the data displayed when new ExtendedBusinessInfoModel is inserted in
    // fun updateData(data: ExtendedBusinessInfoModel?)
    private UesPlaceModel uesPlaceModel = null;
    private ExtendedBusinessInfoModel infoModel = null;

    @Rule
    private String serverResponse = "{\"error\":false,\"place\":{\"id\":77,\"latitudeDbl\":51.181015347158,\"longitudeDbl\":4.6011629328132,\"businessNameStr\":\"My Office\"," +
            "\"businessAddressStr\":\"Alba Iulia 190\\/1 Chisinau, Moldova\",\"businessPhoneStr\":\"+37312345678\",\"businessType\":2,\"contactName\":\"Contact\"," +
            "\"contactEmail\":\"Contact@123.com\",\"contactPhone\":\"+37312345678\",\"openingHours\":\"Monday: 00:00 AM \\u2013 00:00 PM,Tuesday: 00:00 AM \\u2013 00:00 PM," +
            "Wednesday: 00:00 AM \\u2013 00:00 PM,Thursday: 00:00 AM \\u2013 00:00 PM,Friday: 00:00 AM \\u2013 00:00 PM,Saturday: 00:00 AM \\u2013 00:00 PM," +
            "Sunday: 00:00 AM \\u2013 00:00 PM\",\"created_at\":\"2019-01-30 11:44:48\",\"updated_at\":\"0000-00-00 00:00:00\"}}";

    private ActivityTestRule<BusinessInfoActivity> activityRule = new ActivityTestRule<>(BusinessInfoActivity.class);


    public BusinessInfoTesting() throws JSONException {
        uesPlaceModel = ModelsFactory.Companion.createUesPlaceModel(new JSONObject(serverResponse));
        if (uesPlaceModel != null) {
            infoModel = ToExtendedBusinessInfoModel.Companion.getExtendedBusinessInfoModel(uesPlaceModel);
        }
    }

    @Test
    public void updateDataTest() {
        BusinessInfoActivity testActivity = activityRule.getActivity();
        testActivity.updateData(infoModel);
        EditText contactName = testActivity.findViewById(R.id.businessInfoContactName);
        assertThat(contactName.getText().toString(), is("Contact"));
    }
}
