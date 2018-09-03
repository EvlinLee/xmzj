package com.gxtc.huchuan.http;

import android.content.Context;

import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class HttpsUtils {


    private static final String CLIENT_TRUST_PASSWORD = "xinmeizhijia_caisen_hc_666";//信任证书密码
    private static final String CLIENT_AGREEMENT      = "TLS";//使用协议
    private static final String CLIENT_TRUST_MANAGER  = "X509";
    SSLContext sslContext = null;

    public static SSLSocketFactory getSslSocket2() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream is = MyApplication.getInstance().getResources().openRawResource(R.raw.client1);
            KeyStore           keyStore           = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            String certificateAlias = "xmzj";
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(is));
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}