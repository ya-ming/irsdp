package com.yaming.irsdp.ch01.JobHandler;

import com.yaming.irsdp.ch01.Events.Confirm;
import com.yaming.irsdp.ch01.Events.Submit;
import se.sics.kompics.PortType;

public class JobPort extends PortType {{
    request(Submit.class);
    indication(Confirm.class);
}}
