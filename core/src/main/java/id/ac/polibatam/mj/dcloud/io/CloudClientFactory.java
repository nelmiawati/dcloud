package id.ac.polibatam.mj.dcloud.io;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CloudClientFactory {

    public static ICloudClient getCloudClient(final String className, final String accessToken) {

        ICloudClient client = null;
        try {
            final Class<?> clazz = Class.forName(className);
            final Constructor<?> constructor = clazz.getConstructor(String.class);
            client = (ICloudClient) constructor.newInstance(accessToken);
        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {

        } catch (IllegalAccessException e) {

        } catch (InstantiationException e) {

        }
        return client;
    }

}
