package io.chizi.tickethare.util;

import io.chizi.ticket.TicketGrpc;

/**
 * Created by Jiangchuan on 6/4/17.
 */

public interface GrpcRunnable {
    String run(TicketGrpc.TicketBlockingStub blockingStub, TicketGrpc.TicketStub asyncStub) throws Exception;
}
