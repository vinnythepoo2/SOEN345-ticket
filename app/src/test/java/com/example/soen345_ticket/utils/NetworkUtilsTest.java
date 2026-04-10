package com.example.soen345_ticket.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

@RunWith(RobolectricTestRunner.class)
public class NetworkUtilsTest {

    private ConnectivityManager connectivityManager;
    private ShadowConnectivityManager shadowConnectivityManager;

    @Before
    public void setUp() {
        connectivityManager = (ConnectivityManager) RuntimeEnvironment.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        shadowConnectivityManager = shadowOf(connectivityManager);
    }

    @Test
    public void isNetworkAvailable_returnsTrue_whenConnected() {
        NetworkInfo networkInfo = ShadowNetworkInfo.newInstance(
                NetworkInfo.DetailedState.CONNECTED, ConnectivityManager.TYPE_WIFI, 0, true, true);
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo);

        assertTrue(NetworkUtils.isNetworkAvailable(RuntimeEnvironment.getApplication()));
    }

    @Test
    public void isNetworkAvailable_returnsFalse_whenDisconnected() {
        NetworkInfo networkInfo = ShadowNetworkInfo.newInstance(
                NetworkInfo.DetailedState.DISCONNECTED, ConnectivityManager.TYPE_WIFI, 0, true, false);
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo);

        assertFalse(NetworkUtils.isNetworkAvailable(RuntimeEnvironment.getApplication()));
    }

    @Test
    public void isNetworkAvailable_returnsFalse_whenNull() {
        shadowConnectivityManager.setActiveNetworkInfo(null);
        assertFalse(NetworkUtils.isNetworkAvailable(RuntimeEnvironment.getApplication()));
    }

    @Test
    public void isNetworkAvailable_returnsFalse_whenConnectivityManagerIsNull() {
        Context mockContext = mock(Context.class);
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);
        assertFalse(NetworkUtils.isNetworkAvailable(mockContext));
    }
}
