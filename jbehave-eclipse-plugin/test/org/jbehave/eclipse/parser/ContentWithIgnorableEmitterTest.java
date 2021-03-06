package org.jbehave.eclipse.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.jbehave.core.steps.StepType;
import org.jbehave.eclipse.parser.Constants;
import org.jbehave.eclipse.parser.ContentWithIgnorableEmitter;
import org.jbehave.eclipse.parser.StoryPart;
import org.jbehave.eclipse.step.LocalizedStepSupport;
import org.jbehave.eclipse.step.StepCandidate;
import org.jbehave.eclipse.step.ParametrizedStep;
import org.jbehave.eclipse.step.ParametrizedStep.WeightChain;
import org.junit.Before;
import org.junit.Test;

public class ContentWithIgnorableEmitterTest {
    private static final String STEP1 = "Given an account named '$name' with the following properties:$properties";
    public static final String NL = "\n";
    
    public static final String GIVEN1 = "Given an account named 'Travis' with the following properties:" + NL +
            "|key|value|" + NL +
            "!-- Some comment" + NL + 
            "|Login|Travis|" + NL +
            "|Password|p4cm4n|" + NL;

    public static final String EXPECTED1 = "<D>Given an account named '</D><I>Travis</I><D>' with the following properties:</D><I>" + NL +
            "|key|value|" + NL + "</I>" +
            "<C>!-- Some comment" + NL + "</C>" + 
            "<I>|Login|Travis|" + NL +
            "|Password|p4cm4n|" + NL + "</I>";
    
    public static final String GIVEN2 = GIVEN1 +
            NL + 
            "!-- Other comment" + NL;

    public static final String EXPECTED2 = "<D>Given an account named '</D><I>Travis</I><D>' with the following properties:</D><I>" + NL +
            "|key|value|" + NL + "</I>" +
            "<C>!-- Some comment" + NL + "</C>" + 
            "<I>|Login|Travis|" + NL +
            "|Password|p4cm4n|" + NL + 
            "</I>" +
            "<C>" + NL + "!-- Other comment" + NL + "</C>";
    
    private StoryPart storyPart;
    private StepCandidate candidate;
    private ParametrizedStep parametrizedStep;
    private Collector collector;

    private LocalizedStepSupport localizedStepSupport;

    @Before
    public void setUp () {
        localizedStepSupport = new LocalizedStepSupport();
        localizedStepSupport.setStoryLocale(Locale.ENGLISH);
        
        IMethod method = null;
        IAnnotation annotation = null;
        candidate = new StepCandidate(localizedStepSupport, "$", method, annotation, StepType.GIVEN, STEP1, 0);
        parametrizedStep = candidate.getParametrizedStep();
        
        collector = new Collector();
    }
    
    @Test
    public void useCase_exampleTableWithComment_case1 () {
        storyPart = new StoryPart(localizedStepSupport, 17, GIVEN1);
        String rawContent = storyPart.getContent();
        
        ContentWithIgnorableEmitter emitter = new ContentWithIgnorableEmitter(Constants.commentLineMatcher, rawContent);
        String input = emitter.contentWithoutIgnorables();
        
        int offset = 0;
        WeightChain chain = parametrizedStep.calculateWeightChain(input);
        List<String> chainTokens = chain.tokenize();
        for(int i=0;i<chainTokens.size();i++) {
            org.jbehave.eclipse.step.ParametrizedStep.Token pToken = parametrizedStep.getToken(i);
            // be aware that the token length can be shorter than the content length
            // because content can also contain comment :)
            String content = chainTokens.get(i);
            if(pToken.isIdentifier) {
                emitter.emitNext(offset, content.length(), collector, "I");
            }
            else {
                emitter.emitNext(offset, content.length(), collector, "D");
            }
            
            offset += content.length();
        }
        assertThat(collector.emittedList.size(), equalTo(6));
        assertThat(collector.applyOn(rawContent, false), equalTo(rawContent));
        assertThat(collector.applyOn(rawContent, true), equalTo(EXPECTED1));
    }

    @Test
    public void useCase_exampleTableWithComment_case2 () {
        storyPart = new StoryPart(localizedStepSupport, 17, GIVEN2);
        String rawContent = storyPart.getContent();
        
        ContentWithIgnorableEmitter emitter = new ContentWithIgnorableEmitter(Constants.commentLineMatcher, rawContent);
        String input = emitter.contentWithoutIgnorables();
        
        int offset = 0;
        WeightChain chain = parametrizedStep.calculateWeightChain(input);
        List<String> chainTokens = chain.tokenize();
        for(int i=0;i<chainTokens.size();i++) {
            org.jbehave.eclipse.step.ParametrizedStep.Token pToken = parametrizedStep.getToken(i);
            // be aware that the token length can be shorter than the content length
            // because content can also contain comment :)
            String content = chainTokens.get(i);
            if(pToken.isIdentifier) {
                emitter.emitNext(offset, content.length(), collector, "I");
            }
            else {
                emitter.emitNext(offset, content.length(), collector, "D");
            }
            
            offset += content.length();
        }
        assertThat(collector.emittedList.size(), equalTo(7));
        assertThat(collector.applyOn(rawContent, false), equalTo(rawContent));
        assertThat(collector.applyOn(rawContent, true), equalTo(EXPECTED2));
    }
    
    private static class Collector implements ContentWithIgnorableEmitter.Callback<String> {
        public final List<Emitted> emittedList = new ArrayList<Emitted>();
        @Override
        public void emit(String what, int offset, int length) {
            emittedList.add(new Emitted(offset, length, what));
        }
        public String applyOn(String rawContent, boolean surroundWithTokenInfo) {
            StringBuilder builder = new StringBuilder ();
            for(Emitted emitted : emittedList) {
                if(surroundWithTokenInfo) {
                    builder.append("<").append(emitted.what).append(">");
                }
                builder.append(rawContent.substring(emitted.offset, emitted.offset+emitted.length));
                if(surroundWithTokenInfo) {
                    builder.append("</").append(emitted.what).append(">");
                }
            }
            return builder.toString();
        }
        @Override
        public void emitIgnorable(int offset, int length) {
            emit("C", offset, length);
        }
    }
    
    private static class Emitted {
        public final int offset;
        public final int length;
        public final String what;
        public Emitted(int offset, int length, String what) {
            super();
            this.offset = offset;
            this.length = length;
            this.what = what;
        }
    }
    

}
