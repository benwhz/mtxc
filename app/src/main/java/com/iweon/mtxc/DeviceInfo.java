package com.iweon.mtxc;

import java.io.Serializable;
import java.util.UUID;

enum SwitchType{
    OneToOne,
    OneToAll,
    OneToMulti
}

public class DeviceInfo  implements Serializable {
    UUID uuid;
    String name;
    byte id;

    int inCount;
    int outCount;

    int showInCount;
    int showOutCount;

    String ip;
    int port;

    SwitchType switchType;

    int timeout;
}
