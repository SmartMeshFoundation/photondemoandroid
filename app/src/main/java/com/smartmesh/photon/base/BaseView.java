package com.smartmesh.photon.base;

public interface BaseView {

    void onResult(Object result, String message);

    void onError(Throwable throwable, String message);

}
