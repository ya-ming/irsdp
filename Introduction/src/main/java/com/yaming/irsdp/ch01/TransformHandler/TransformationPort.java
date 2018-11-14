package com.yaming.irsdp.ch01.TransformHandler;

import com.yaming.irsdp.ch01.Events.*;
import com.yaming.irsdp.ch01.Events.Error;
import se.sics.kompics.PortType;

public class TransformationPort extends PortType{{
    request(Submit.class);
    indication(Confirm.class);
    indication(Error.class);
}}
