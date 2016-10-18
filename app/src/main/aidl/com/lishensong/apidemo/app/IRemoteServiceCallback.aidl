// IRemoteServiceCallback.aidl
package com.lishensong.apidemo.app;

// Declare any non-default types here with import statements


// Example of a callback interface used by IRemoteService to send
//synchronour notfications back to its clients. Note that this is
// a one-way interface so the server dose not block waiting for the client.
interface IRemoteServiceCallback {
    void valueChanged(int value);
}
