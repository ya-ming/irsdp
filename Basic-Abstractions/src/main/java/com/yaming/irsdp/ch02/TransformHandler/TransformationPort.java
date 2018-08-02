package com.yaming.irsdp.ch02.TransformHandler;

import com.yaming.irsdp.ch02.Events.*;
import com.yaming.irsdp.ch02.Events.Error;
import se.sics.kompics.PortType;

public class TransformationPort extends PortType{{
    request(Submit.class);
    indication(Confirm.class);
    indication(Error.class);
}}
