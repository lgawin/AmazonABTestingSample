package amazonab.sample.foqus.com.pl.amazonabtestingsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amazon.insights.ABTestClient;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.EventClient;
import com.amazon.insights.InsightsCallback;
import com.amazon.insights.InsightsCredentials;
import com.amazon.insights.InsightsOptions;
import com.amazon.insights.Variation;
import com.amazon.insights.VariationSet;
import com.amazon.insights.error.InsightsError;


public class HomeActivity extends Activity implements View.OnClickListener {

    private static final String AMAZON_PUBLIC_KEY = "b46bcc7b7b024f2890cebf719e4915bc";
    private static final String AMAZON_PRIVATE_KEY = "Yki1myPob67oziZExoib+LJgVbIB/62n2zNTCHgTvGQ=";
    public static final String AB_VARIATION = "Home screen appearance";

    private static final String LOG_TAG = HomeActivity.class.toString();

    private ABTestClient abClient;
    private EventClient eventClient;
    private View button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InsightsCredentials credentials = AmazonInsights.newCredentials(AMAZON_PUBLIC_KEY, AMAZON_PRIVATE_KEY);
        InsightsOptions options = AmazonInsights.newOptions(true, true);
        AmazonInsights insightsInstance = AmazonInsights.newInstance(credentials, getApplicationContext(), options);

        abClient = insightsInstance.getABTestClient();
        eventClient = insightsInstance.getEventClient();

        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        button.setVisibility(View.GONE);

        abClient.getVariations(AB_VARIATION).setCallback(new InsightsCallback<VariationSet>() {
            @Override
            public void onComplete(VariationSet variations) {
                Variation variation = variations.getVariation(AB_VARIATION);

                if (!variation.getVariableAsBoolean("hasIcon", true)) {
                    findViewById(R.id.button_icon).setVisibility(View.GONE);
                }

                if (!variation.getVariableAsBoolean("hasLabel", true)) {
                    findViewById(R.id.button_label).setVisibility(View.GONE);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(InsightsError error) {
                super.onError(error);
                Log.w(LOG_TAG, error.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventClient.recordEvent(eventClient.createEvent("HomeActivityView"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventClient.submitEvents();
    }

    @Override
    public void onClick(View v) {
        eventClient.recordEvent(eventClient.createEvent("HomeButtonClick"));
    }
}
