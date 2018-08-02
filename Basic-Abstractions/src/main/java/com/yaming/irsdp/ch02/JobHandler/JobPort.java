package com.yaming.irsdp.ch02.JobHandler;

import com.yaming.irsdp.ch02.Events.Confirm;
import com.yaming.irsdp.ch02.Events.Submit;
import se.sics.kompics.PortType;

public class JobPort extends PortType {{
    request(Submit.class);
    indication(Confirm.class);
}}
