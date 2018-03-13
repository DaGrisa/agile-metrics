package at.grisa.agilemetrics.mockserver;

import org.mockserver.integration.ClientAndServer;

import java.util.HashSet;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class MockServer {
    private static MockServer instance;
    private ClientAndServer mockServer;
    private HashSet<Class> activeClasses;

    private final int PORT = 1080;

    private MockServer() {
        activeClasses = new HashSet<>();
    }

    public static MockServer getInstance() {
        if (instance == null) {
            instance = new MockServer();
        }
        return instance;
    }

    public void startClass(Class clazz) {
        if (mockServer == null) {
            mockServer = startClientAndServer(PORT);
        }
        activeClasses.add(clazz);
    }

    public void stopClass(Class clazz) {
        activeClasses.remove(clazz);
        if (activeClasses.size() == 0) {
            mockServer.stop();
        }
    }

    public int getPort() {
        return PORT;
    }

}
