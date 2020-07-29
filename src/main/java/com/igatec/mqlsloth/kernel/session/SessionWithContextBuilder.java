package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.iface.kernel.SessionBuilder;
import com.igatec.mqlsloth.kernel.SlothException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SessionWithContextBuilder implements SessionBuilder {
    protected Context context;

    static void setTrustManager() throws Exception {
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(
                (KeyManager[]) null,
                new TrustManager[]{new SessionWithContextBuilder.TrustAllTrustManager()},
                (SecureRandom) null
        );
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public static class TrustAllTrustManager implements X509TrustManager {
        public TrustAllTrustManager() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRemoteContext(String host, String user, String password) throws SlothException {
        try {
            setTrustManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Context context = null;
        try {
            context = Context.instance(host);
            context.setUser(user);
            context.setPassword(password);
            context.connect();
            if (!context.isConnected()) {
                throw new SlothException("Context connection error");
            }
            this.context = context;
        } catch (Exception e) {
            throw new SlothException(e);
        }
    }

    @Override
    public Session build() throws SlothException {
        return new SessionWithContext(context);
    }
}
