package me.hao0.trace.core.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.*;
import com.google.common.base.Strings;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.BinaryAnnotation;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;
import me.hao0.trace.core.*;
import me.hao0.trace.core.config.TraceConf;
import me.hao0.trace.core.config.TraceConfLoader;
import me.hao0.trace.core.util.Ids;
import me.hao0.trace.core.util.ServerInfo;
import me.hao0.trace.core.util.Times;

import java.util.Map;

/**
 * 该版本封装
 * Trace Service Filter
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class TraceProviderFilter implements Filter {

    private TraceConf conf = TraceConfLoader.load("trace.yml");

    private TraceAgent agent = new TraceAgent(conf.getServer());

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if (!conf.getEnable()) {
            // not enable tracing
            return invoker.invoke(invocation);
        }


        //        if (!attaches.containsKey(TraceConstants.TRACE_ID)) {
        //            // don't need tracing
        //            // TODO 没有开始跟踪就在此处开始
        //            return invoker.invoke(invocation);
        //        }

        // prepare trace context
        startTrace(invoker, invocation);
        try {
            Result result = invoker.invoke(invocation);
            return result;
        } finally {
            endTrace();
        }
    }

    private void startTrace(Invoker<?> invoker, Invocation invocation) {

        Map<String, String> attaches = invocation.getAttachments();
        Long traceId = attaches.get(TraceConstants.TRACE_ID) == null ?
            null :
            Long.parseLong(attaches.get(TraceConstants.TRACE_ID));
        Long parentSpanId = attaches.get(TraceConstants.SPAN_ID) == null ?
            null :
            Long.parseLong(attaches.get(TraceConstants.SPAN_ID));

        // start tracing
        TraceContext.start();

        if (traceId == null) {

            traceId = Ids.get();
            parentSpanId = traceId;



            Span dubboSpan = new Span();

            // span basic data
            dubboSpan.setId(traceId);
            dubboSpan.setTrace_id(traceId);
            String name = invoker.getUrl().getServiceInterface() + "." + invocation.getMethodName();
            dubboSpan.setName(name);
            long timestamp = Times.currentMicros();
            dubboSpan.setTimestamp(timestamp);

            // sr annotation
            dubboSpan.addToAnnotations(Annotation.create(timestamp, TraceConstants.ANNO_SR,
                Endpoint.create(name, ServerInfo.IP4, invoker.getUrl().getPort())));

            // app name
            dubboSpan.addToBinary_annotations(BinaryAnnotation.create("name", name, null));

            // app owner
            dubboSpan.addToBinary_annotations(
                BinaryAnnotation.create("owner", invoker.getUrl().getParameter("owner"), null));

            // attach trace data
            attaches.put(TraceConstants.TRACE_ID, String.valueOf(traceId));
            attaches.put(TraceConstants.SPAN_ID, String.valueOf(traceId));
            TraceContext.addSpan(dubboSpan);

        }

        TraceContext.setTraceId(traceId);
        TraceContext.setSpanId(parentSpanId);
    }

    private void endTrace() {
        agent.send(TraceContext.getSpans());
        TraceContext.clear();
    }
}
