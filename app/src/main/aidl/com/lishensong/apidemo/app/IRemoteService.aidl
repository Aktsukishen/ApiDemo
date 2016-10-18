// IRemoteService.aidl
package com.lishensong.apidemo.app;

// Declare any non-default types here with import statements
import com.lishensong.apidemo.app.IRemoteServiceCallback;

interface IRemoteService {
    // Often you want to allow a service to call back to its clients.
     //This shows how to do so ,by registering a callback interface
     //with the service
    void registerCallback(IRemoteServiceCallback cb);

    void unregisterCallback(IRemoteServiceCallback cb);
}
