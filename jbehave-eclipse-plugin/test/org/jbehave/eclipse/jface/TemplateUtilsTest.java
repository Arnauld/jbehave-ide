package org.jbehave.eclipse.jface;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jbehave.eclipse.jface.TemplateUtils.templatizeVariables;

import org.junit.Test;

public class TemplateUtilsTest {

    @Test 
    public void templatizeVariables_ () {
        assertThat(templatizeVariables("$username"), equalTo("${username}"));
        assertThat(templatizeVariables("a user named $username clicks on $button button"), equalTo("a user named ${username} clicks on ${button} button"));
        assertThat(templatizeVariables("a user named bob clicks on enter button"), equalTo("a user named bob clicks on enter button"));
    }
}
