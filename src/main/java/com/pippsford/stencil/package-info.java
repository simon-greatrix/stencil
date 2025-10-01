/**
 * <h2 id="the-stencil-templating-system">The Stencil Templating System.</h2>
 * <p>The Stencil Template System is used to produce text to represent data, such as a web page or report. It is primarily intended to be very simple to
 * learn.</p>
 * <p>A stencil comprises the output text with embedded simple processing instructions.</p>
 * <p>All data for a stencil must be prepared in advance as a stencil cannot contain embedded code.</p>
 * <h3 id="example">Example</h3>
 * <p>Input:</p>
 * <pre><code>appliance: &quot;lamp&quot;
 * isTurnedOn: true
 * </code></pre>
 * <p>Stencil:</p>
 * <pre><code>The {appliance} is [if isTurnedOn]on[else]off[end].
 * </code></pre>
 * <p>Output:</p>
 * <pre><code>The lamp is on.
 * </code></pre>
 * <h2 id="the-input-data">The input data</h2>
 * <p>The data is presented to the stencil as a map of name-value pairs. The value may be any data type including structures such as collections and maps.</p>
 * <p>The value identifier should be limited to the normal characters legal in identifiers.</p>
 * <p>As a guide alphanumeric characters are fine but braces, brackets, and commas must be avoided.</p>
 * <p>Specifically, an identifier can contain:</p>
 * <ul>
 * <li>Any Unicode alphabetic character</li>
 * <li>Any Unicode decimal number character</li>
 * <li>Any Unicode connector punctuation</li>
 * <li>Any Unicode marks (i.e. accents and decorations)</li>
 * <li>Any Unicode currency symbol</li>
 * <li>The solidus character (&quot;/&quot;)</li>
 * <li>The period character (&quot;.&quot;) which has a special interpretation.</li>
 * </ul>
 * <h3 id="meaning-of-period-">Meaning of period (&quot;.&quot;)</h3>
 * <p>The data is assumed to contain structures equivalent to dictionaries and arrays. The period is used as a path separator to traverse the input data
 * structures.</p>
 * <p>For example, given this input JSON:</p>
 * <pre><code class="language-json">{
 *   &quot;a&quot;: [
 *     {
 *       &quot;b&quot;: 1,
 *       &quot;c&quot;: false
 *     },
 *     true,
 *     {
 *       &quot;d&quot;: 2
 *     }
 *  ]
 * }
 * </code></pre>
 * <p>Then:</p>
 * <ul>
 * <li><code>&quot;a.0.b&quot; == 1</code></li>
 * <li><code>&quot;a.1&quot; == true</code></li>
 * <li><code>&quot;a.2&quot; == { &quot;d&quot;:2 }</code></li>
 * </ul>
 * <h3 id="inheritance">Inheritance</h3>
 * <p>Every level of the input structure inherits from its container. This means that the following identities also hold:</p>
 * <ul>
 * <li><code>&quot;a.0.1&quot; == true</code> (<code>&quot;a.0&quot;</code> does not have a property &quot;1&quot;, so it inherits it)</li>
 * <li><code>&quot;a.2.0.d&quot; == 2</code> (<code>&quot;a.2&quot;</code> does not have a property <code>&quot;0&quot;</code>,
 *  so it inherits it from <code>&quot;a&quot;</code>, and <code>&quot;a.0&quot;</code> does not have a property <code>&quot;d&quot;</code>, so it inherits if
 *  from <code>&quot;a.2&quot;</code>)</li>
 * </ul>
 * <h3 id="handling-of-maps">Handling of Maps</h3>
 * <p>If part of the input data is a map, then its contents will be accessed using the relevant section of the input identifier to select a value from the
 * map.</p>
 * <p>If the map does not specify a value for <code>&quot;isEmpty&quot;</code>, then this value returns a Boolean indicating if the map is empty or not.</p>
 * <p>If the map does not specify a value for <code>&quot;size&quot;</code>, then this value returns the number of entries in the map.</p>
 * <h3 id="handling-of-lists">Handling of Lists</h3>
 * <p>If the input is a list, array or an iterable, then it is values can be accessed using a zero-based index. A <code>&quot;size&quot;</code> and an
 * <code>&quot;isEmpty&quot;</code> properties are also available.</p>
 * <h3 id="everything-else">Everything Else</h3>
 * <p>If the input is not a map, array, list, nor iterable, then it is treated as Java Bean, and its properties can be access according to the normal
 * conventions for such.</p>
 * <h2 id="literals-values-and-directives">Literals, Values and Directives</h2>
 * <p>A stencil contains literal text, which is simply copied to the output, values which are replaced with data from the input, and directives which control
 * processing.</p>
 * <p>A value is always enclosed in braces, the <code>{</code> and <code>}</code> characters.</p>
 * <p>A directive is always enclosed in brackets, the <code>[</code> and <code>]</code> characters.</p>
 * <h3 id="whitespace">Whitespace</h3>
 * <p>Whitespace is ignored around the parameters inside a value.</p>
 * <p>Whitespace is ignored around the keywords and parameters of a directive.</p>
 * <p>Whitespace is ignored between two directives if it contains a new-line.</p>
 * <p>This last rule is to enable formatting of stencils to improve readability without compromising the output and is best explained by an example. Compare
 * the following two equivalent stencils:</p>
 * <p>The nicely formatted version that ignores whitespace that contains a new-line between directives:</p>
 * <pre><code>Headers:
 *  --------
 * [loop headers]
 *   [loop value]
 *     [apply F.index]
 *     [if isFirst]
 *       [**]{key,format,%-15s} : {value}
 * [**]
 *     [else]
 *       [**]                : {value}
 * [**]
 *     [end]
 *   [end]
 * [end]
 * </code></pre>
 * <p>Note the use of directive comments <code>[**]</code> to separate ignorable whitespace from required whitespace.</p>
 * <p>The not nicely formatted version that does not include ignorable white space:</p>
 * <pre><code>Headers:
 * --------
 * loop headers][loop value][apply F.index][if isFirst]{key,format,%-15s} : {value}
 * [else]                : {value}
 * [end][end][end]
 * </code></pre>
 * <h2 id="comments">Comments</h2>
 * <p>Stencils may include comments. There are two kinds.</p>
 * <p><code>[* This is a directive comment. *]</code></p>
 * <p><code>{* This is a value comment. *}</code></p>
 * <p>The only difference between the two comment styles is how they affect surrounding whitespace. Remember that whitespace containing a new-line is ignored
 * between two directives:</p>
 * <pre><code>[* This is a directive comment. *]
 *     [* The preceding whitespace was ignored. *]
 * </code></pre>
 * <pre><code>{* This is a value comment. *}
 *     [* The preceding whitespace was not ignored. *]
 * </code></pre>
 * <p>This can be useful as in the example above to make formatting easier, or to preserve necessary line breaks. For example, this <code>if</code> directive
 * never outputs a new-line:</p>
 * <pre><code>[if newLine]
 * [end]
 * </code></pre>
 * <p>But this one does:</p>
 * <pre><code>[if newLine]
 * {**}[end]
 * </code></pre>
 * <h2 id="directives">Directives</h2>
 * <h3 id="the-if-directive">The &quot;if&quot; directive</h3>
 * <pre><code>[if value] … value is true … [end]
 *
 * [if value] … value is true … [else] … value is false … [end]
 * </code></pre>
 * <p>The <code>if</code> directive is used to process part of the stencil only if a value is true, and optionally process part of the stencil only if a value
 * is false.</p>
 * <p>Value is not an expression, it is a value in the input data. The value is equivalent to true if it is <strong>not</strong> any of the following:</p>
 * <ul>
 * <li>Boolean False</li>
 * <li>Null</li>
 * <li>The empty string</li>
 * <li>A string that equals &quot;false&quot;, ignoring case</li>
 * <li>A floating point Not-a-number (NaN)</li>
 * <li>Zero</li>
 * </ul>
 * <p>For example, all the following are equivalent to true:</p>
 * <ul>
 * <li>Boolean.TRUE</li>
 * <li>86</li>
 * <li>new HashMap&lt;&gt;()</li>
 * <li>&quot;calculator&quot;</li>
 * </ul>
 * <h3 id="the-loop-directive">The &quot;loop&quot; directive</h3>
 * <p>This is used to process every entry in a collection or map. When processing a map, the values are the contents of the map&#39;s entry set.</p>
 * <pre><code>[loop map]
 *   {index} : {key} is mapped to {value}
 * [else]
 *   There is no map, or it is empty.
 * [end]
 * </code></pre>
 * <p>Within a loop there are only four values available (though others are inherited):</p>
 * <ol>
 * <li><code>value</code> – the current value of the list, or the value of the map entry</li>
 * <li><code>key</code> – only present for maps where it is the current map key</li>
 * <li><code>index</code> – the zero based index</li>
 * <li><code>size</code> – the number of entries in this loop</li>
 * </ol>
 * <p>The &quot;else&quot; block is invoked if the value is missing or empty.</p>
 * <h3 id="the-include-directive">The &quot;include&quot; directive</h3>
 * <p>Code re-use is good. The &quot;include&quot; directive allows another stencil to be included in the current stencil.</p>
 * <pre><code>[include ../path/general]
 * </code></pre>
 * <p>The path is a literal. It may be absolute or relative.</p>
 * <p>Dynamic includes are not supported.</p>
 * <h4 id="resource-bundles">Resource Bundles</h4>
 * <p>Code can also be included from resource bundles. The syntax is:</p>
 * <pre><code>[{ _key_ }]
 * [{ _bundle_, _key_ }]
 * </code></pre>
 * <p>The first form retrieves an entry identified by <code>_key_</code> from the current resource bundle. The current resource bundle may be set with the
 * <code>set</code> directive.</p>
 * <p>The second form specifies the resource bundle to use as well as the key.</p>
 * <p>The text retrieved from the resource bundle is parsed as a stencil and processed in place.</p>
 * <p>Stencils drawn from resource bundles cannot use the <code>include</code> directive, but can include other resource bundle entries.</p>
 * <h3 id="the-use-directive">The &quot;use&quot; directive</h3>
 * <p>Specifying the full path to a value can be tiresome, or limit code re-use. The <code>use</code> directive switches the root to the specified datum.</p>
 * <pre><code>[use mapValue] … [else] … [end]
 * [use otherValue] … [end]
 * </code></pre>
 * <p>An optional <code>[else]</code> block may be given. This optional block is processed if the datum is missing or null.</p>
 * <h3 id="the-set-directive">The &quot;set&quot; directive</h3>
 * <p>The <code>set</code> directive is used to specify one or more processing parameters.</p>
 * <pre><code>[set bundle=api_messages, escape=html_strict]
 * </code></pre>
 * <h4 id="the-bundle-parameter">The &quot;bundle&quot; parameter</h4>
 * <p>This is used to specify the default resource bundle from which messages may be drawn. Use of resource bundles will be explained later.</p>
 * <h4 id="the-escape-parameter">The &quot;escape&quot; parameter</h4>
 * <p>Sets the current character escaping system for values. Possible escape systems are explained later.</p>
 * <h3 id="the-apply-directive">The &quot;apply&quot; directive</h3>
 * <p>The input data may contain functions which can be invoked during processing. The <code>apply</code> directive makes this possible.</p>
 * <pre><code>[apply isLess = F.is( a, &#39;LT&#39;, b )]
 * [if isLess] &#39;a&#39; is less than &#39;b&#39; [end]
 * </code></pre>
 * <p>A function does not have to return a single value. It can also manipulate the current data directly.</p>
 * <p>The parameters to the function are either references to input data, or literals enclosed in single quotes. Numerical literals are still enclosed in
 * single quotes.</p>
 * <p>Creating functions is an advanced topic, not covered here.</p>
 * <p>See &quot;Common Functions&quot; for descriptions of commonly available functions.</p>
 * <h2 id="values">Values</h2>
 * <p>As stencil templates are for displaying data, there are obviously many ways to display and format data.</p>
 * <p>The table explains the basics:</p>
 * <table>
 * <caption>Outputting values</caption>
 * <thead>
 * <tr>
 * <th>Syntax</th>
 * <th>Example</th>
 * <th>Purpose</th>
 * </tr>
 * </thead>
 * <tbody><tr>
 * <td><code>{value}</code></td>
 * <td><code>{value}</code></td>
 * <td>Outputs the value by invoking the Java toString method on it. Nulls are output as an empty string.</td>
 * </tr>
 * <tr>
 * <td><code>{value, date, style}</code></td>
 * <td><code>{value, date, iso-ordinal}</code></td>
 * <td>Accepts a date value and formats it according to the specified style. Date-time formats listed in Appendix 1.The second form allows for formats that
 * include</td>
 * </tr>
 * <tr>
 * <td><code>{value, time, _style_}</code></td>
 * <td><code>{value, time, short}</code></td>
 * <td>Accepts a time value and formats it according to the specified style. Date and time formats in Appendix 1.</td>
 * </tr>
 * <tr>
 * <td><code>{value, datetime, _style_}</code></td>
 * <td><code>{value, datetime, rfc822}</code></td>
 * <td>Accepts a date and time value and formats it according to the specified style. Date and time formats in Appendix 1.</td>
 * </tr>
 * <tr>
 * <td><code>{value, datetime, _date-style_, _time-style_}</code></td>
 * <td><code>{value, datetime, medium, short}</code></td>
 * <td>Accepts a date and time and formats it according to the specified date style and the specified time style. Only the basic &quot;short&quot;,
 * &quot;medium&quot;, &quot;long&quot;, and &quot;full&quot; styles are supported here.</td>
 * </tr>
 * <tr>
 * <td><code>{value, format, _style_}</code></td>
 * <td><code>{value, format, %15s}</code></td>
 * <td>Apply a &quot;String.format&quot; style format to the value. If the value is null, an empty string is returned.</td>
 * </tr>
 * <tr>
 * <td><code>{value, number, _style_}</code></td>
 * <td><code>{value, number, ¤#,##0.00}</code></td>
 * <td>Apply a number format to the value. Available number formats are listed in Appendix 2.</td>
 * </tr>
 * </tbody></table>
 * <h3 id="here-documents">&quot;Here Documents&quot;</h3>
 * <p>A &quot;here document&quot; takes the form:</p>
 * <pre><code>{&gt;&gt; _marker_} … any text … {_marker_}
 * </code></pre>
 * <p>Where <em>marker</em> is any identifier that such that &quot;{<em>marker</em>}&quot; does not appear in the text. The text inside the document is
 * outputted exactly as it is present.</p>
 * <p>For example:</p>
 * <pre><code>{ &gt;&gt; !}&lt;% {value} %&gt;{!}
 * </code></pre>
 * <p>Produces the literal output, with HTML escaping:</p>
 * <pre><code>&amp;lt;% {value} %&amp;gt;
 * </code></pre>
 * <p>&quot;Here documents&quot; may also be used to specify a style that contains troublesome patterns. For example:</p>
 * <pre><code>{value, datetime, &gt;&gt; X} {YYYY}-{MM}-{DD} {X}
 * </code></pre>
 * <p>Produces the output:</p>
 * <pre><code>{2020}-{05}-{23}
 * </code></pre>
 * <h3 id="escaping-special-characters">Escaping special characters</h3>
 * <p>The escape style is applied to all values and &quot;here documents&quot;. Literal text in the stencil is not escaped. There are many built-in escape
 * styles and advanced users can add others.</p>
 * <p>To specify an escape style one can either use the set directive:</p>
 * <pre><code>[set escape = _style_]
 * </code></pre>
 * <p>Or one can specify it just for a single value:</p>
 * <pre><code>{_style_ : value,format,%15s}
 * </code></pre>
 * <p>All values take this optional escape style specifier.</p>
 * <p>Built-in escape styles are listed in Appendix 3</p>
 * <h2 id="stencil-compendia">Stencil Compendia</h2>
 * <p>Stencils are stored in compendium files. They typically have a &quot;.comp&quot; suffix.</p>
 * <p>A compendium is a text file that can be loaded from the class path. Each compendium can contain multiple stencils. Each stencil in the file is preceded
 * by its identifying path.</p>
 * <p>Normally any line starting with a &#39;/&#39; is assumed to be a path specification.</p>
 * <p>Whitespace after a stencil is normally ignored.</p>
 * <p>A path:</p>
 * <ul>
 * <li>Starts with a &#39;/&#39;.</li>
 * <li>Does not end with a &#39;/&#39;, unless it is just &quot;/&quot;.</li>
 * <li>Cannot contain these characters: &#39;{&#39;, &#39;}&#39;, &#39;&lt;&#39;, &#39;&gt;&#39;, &#39;[&#39;, &#39;]&#39;, &#39;:&#39;, &#39;;&#39;,
 * &#39;&#39;</li>
 * </ul>
 * <p>Example:</p>
 * <pre><code>/this/is/a/path
 *
 * This is the text of a stencil.
 *
 * It carries on until a line starts with a &#39;/&#39;.
 *
 * /this/is/another/stencil \&gt;\&gt; END-OF
 *
 * This is a second stencil, using a &quot;here document&quot;.
 *
 * /this/is/NOT/A/PATH
 *
 * The above line does not start a new stencil because we are still within the &quot;here document&quot;.
 *
 * The following line ends the &quot;here document&quot;.
 *
 * END-OF
 *
 * /this/is/a/third/stencil
 *
 * A third stencil in this compendium.
 *
 * /this/has/trailing/whitespace \&gt;\&gt; END-OF
 *
 * This stencil has preserved trailing whitespace because it is inside a &quot;here document&quot;.
 *
 * END-OF
 * </code></pre>
 * <p>The end of a &quot;here document&quot; is indicated by putting the marker on its own on a line. Trailing whitespace before the marker is preserved.
 * Trailing whitespace after the marker is ignored.</p>
 * <h2 id="common-functions">Common Functions</h2>
 * <h3 id="the-index-function">The &quot;index&quot; function</h3>
 * <p>The index function is used to provide useful additional values inside the <code>[loop]</code> directive.</p>
 * <pre><code>[loop data]
 *     [apply index]
 *     Write information about the data.
 * [end]
 * </code></pre>
 * <pre><code>[loop data]
 *     [apply index(,10)]
 *     Write information about the data with a page size of 10.
 * [end]
 * </code></pre>
 * <pre><code>[loop data]
 *     [apply index(prefix,10)]
 *     Write information about the data with a page size of 10.
 *     The new values are prefixed with &quot;prefix&quot;.
 * [end]
 * </code></pre>
 * <pre><code>[loop data]
 *     [apply index(prefix)]
 *     Write information about the data with no paging.
 *     The new values are prefixed with &quot;prefix&quot;.
 * [end]
 * </code></pre>
 * <p>The following values are added to the loop&#39;s data context:</p>
 * <table>
 * <caption>Loop values</caption>
 * <thead>
 * <tr>
 * <th><strong>Property</strong></th>
 * <th><strong>Description</strong></th>
 * </tr>
 * </thead>
 * <tbody><tr>
 * <td><code>isFirst</code></td>
 * <td>True if this is the first entry in the loop</td>
 * </tr>
 * <tr>
 * <td><code>isLast</code></td>
 * <td>True if this is the last entry in the loop</td>
 * </tr>
 * <tr>
 * <td><code>index1</code></td>
 * <td>The index plus 1, for a one-based index instead of a zero based index</td>
 * </tr>
 * <tr>
 * <td><code>isEven</code></td>
 * <td>True if the index is even</td>
 * </tr>
 * <tr>
 * <td><code>isOdd</code></td>
 * <td>True if the index is odd</td>
 * </tr>
 * </tbody></table>
 * <p>If a page size has been set, then the following additional values are also set:</p>
 * <table>
 * <caption>Page values</caption>
 * <thead>
 * <tr>
 * <th><strong>Property</strong></th>
 * <th><strong>Description</strong></th>
 * </tr>
 * </thead>
 * <tbody><tr>
 * <td><code>isFirstOnPage</code></td>
 * <td>True if this is the first entry on the current page</td>
 * </tr>
 * <tr>
 * <td><code>isLastOnPage</code></td>
 * <td>True if this is the last entry on the current page</td>
 * </tr>
 * <tr>
 * <td><code>pageRow</code></td>
 * <td>The row on the page (starting from 1)</td>
 * </tr>
 * <tr>
 * <td><code>pageNumber</code></td>
 * <td>The current page number (starting from 1)</td>
 * </tr>
 * <tr>
 * <td><code>pageCount</code></td>
 * <td>The number of pages required.</td>
 * </tr>
 * </tbody></table>
 * <h3 id="the-is-function">The &quot;is&quot; function</h3>
 * <p>The &quot;is&quot; function is used to compare two values and returns a Boolean.</p>
 * <p>Test if the values are equal</p>
 * <pre><code>[apply result = is(value1, &#39;EQ&#39;, value2)]
 * </code></pre>
 * <p>Test if the values are not equal</p>
 * <pre><code>[apply result = is(value1, &#39;NE&#39;, value2)]
 * </code></pre>
 * <p>Test if <code>value1</code> is less than <code>value2</code></p>
 * <pre><code>[apply result = is(value1, &#39;LT&#39;, value2)]
 * </code></pre>
 * <p>Test if value1 is less than or equal to value2</p>
 * <pre><code>[apply result = is(value1, &#39;LE&#39;, value2)]
 * </code></pre>
 * <p>Test if value1 is greater than value2</p>
 * <pre><code>[apply result = is(value1, &#39;GT&#39;, value2)]
 * </code></pre>
 * <p>Test if value1 is greater than or equal to value2</p>
 * <pre><code>[apply result = is(value1, &#39;GE&#39;, value2)]
 * </code></pre>
 * <p>Test if a value is less than 10</p>
 * <pre><code>[apply result = is(value1, &#39;LT&#39;, &#39;10&#39;)
 * </code></pre>
 * <p>The result of the comparison can then be used in an <code>[if]</code> directive.</p>
 * <p>If the values are both numbers, then they are compared as numbers. Otherwise, they are compared as Strings.</p>
 * <h3 id="the-for-function">The &quot;for&quot; function</h3>
 * <p>The &quot;for&quot; function is used to generate a list of integers that can be used to trigger a fixed size loop.</p>
 * <pre><code>[apply oneToTen = for(&#39;1&#39;,&#39;11&#39;)]
 * [loop oneToTen] {value} [end]
 * </code></pre>
 * <pre><code>[apply count = for(countSize)]
 * </code></pre>
 * <pre><code>[apply count = for(start,end,step)]
 * </code></pre>
 * <p>The &quot;for&quot; function takes one to three arguments.</p>
 * <p>With one argument, the generated integers count from zero up to one less than the value given. If the value is negative or zero the generated list is
 * empty.</p>
 * <p>With two arguments, the generated integers count from the first value up to one less than the second value. If the second value is less than or equal to
 * the first, the generated list is empty.</p>
 * <p>With three arguments, the generated integers count from the first value to the second value in steps as indicated by the third value. The step may be
 * negative in size.</p>
 * <h3 id="the-stack-trace-function">The Stack Trace function</h3>
 * <p>The stack trace returns the result of invoking the &quot;printStackTrace&quot; method on a Throwable.</p>
 * <pre><code>[apply trace = stackTrace(throwable)]
 * </code></pre>
 * <h2 id="appendix-1--list-of-supported-date-and-time-formats">Appendix 1 – List of supported date and time formats.</h2>
 * <p>Date and time format specifiers are case-insensitive. Hyphens may be removed, or replaced by whitespace, periods, or underscores. The &quot;iso&quot;
 * prefix is optional.</p>
 * <ul>
 * <li><p>Iso-basic</p>
 * </li>
 * <li><p>Iso-date</p>
 * </li>
 * <li><p>Iso-date-time</p>
 * </li>
 * <li><p>Iso-time</p>
 * </li>
 * <li><p>Iso-basic-date</p>
 * </li>
 * <li><p>Iso-offset-date</p>
 * </li>
 * <li><p>Iso-offset-time</p>
 * </li>
 * <li><p>Iso-local-date</p>
 * </li>
 * <li><p>Iso-local-time</p>
 * </li>
 * <li><p>Iso-zoned</p>
 * </li>
 * <li><p>Iso-zoned-date-time</p>
 * </li>
 * <li><p>Iso-ordinal</p>
 * </li>
 * <li><p>Iso-ordinal-date</p>
 * </li>
 * <li><p>Iso-week-date</p>
 * </li>
 * <li><p>Iso-instant</p>
 * </li>
 * <li><p>Rfc</p>
 * </li>
 * <li><p>Rfc-1123</p>
 * </li>
 * <li><p>Rfc-822</p>
 * </li>
 * <li><p>Rfc-1123-date-time</p>
 * </li>
 * <li><p>Short</p>
 * </li>
 * <li><p>Medium</p>
 * </li>
 * <li><p>Long</p>
 * </li>
 * <li><p>Full</p>
 * </li>
 * </ul>
 * <p>For explanations of the ISO and RFC styles, see the Java <code>DateTimeFormatter</code> documentation.</p>
 * <p>For explanations of the short, medium, long, and full styles, see Java <code>FormatStyle</code>.</p>
 * <p>The style may also be any pattern acceptable to the Java <code>DateTimeFormatter.ofPattern</code> method.</p>
 * <h2 id="appendix-2--list-of-supported-number-formats">Appendix 2 – List of supported number formats</h2>
 * <p>The following standard number formats are supported:</p>
 * <ul>
 * <li>Currency</li>
 * <li>Percent</li>
 * <li>Integer</li>
 * <li>Number</li>
 * </ul>
 * <p>One can also specify any pattern acceptable to Java <code>DecimalFormat</code>.</p>
 * <h2 id="appendix-3--list-of-supported-escape-styles">Appendix 3 – List of supported escape styles.</h2>
 * <p>Escape styles are case-insensitive. The hyphen may be removed, or replaced with a period, underscore, or whitespace.</p>
 * <table>
 * <caption>Escape styles</caption>
 * <thead>
 * <tr>
 * <th><strong>Style</strong></th>
 * <th><strong>Description</strong></th>
 * </tr>
 * </thead>
 * <tbody><tr>
 * <td>No-Escape<br>No<br>None</td>
 * <td>Output the text with no changes</td>
 * </tr>
 * <tr>
 * <td>HTML-Safe</td>
 * <td>Ensures there are no dangerous characters in the text but leaves valid SGML character entities untouched.<br>Example: <code>&lt;&amp;amp;&gt;</code>
 * becomes <code>&amp;lt;&amp;amp;&amp;gt;</code></td>
 * </tr>
 * <tr>
 * <td>HTML-Strict</td>
 * <td>Replaces all special characters with the appropriate SGML character entities.<br>Example: <code>&lt;&amp;amp;&gt;</code> becomes
 * <code>&amp;lt;&amp;amp;amp;&amp;gt;</code></td>
 * </tr>
 * <tr>
 * <td>ECMA</td>
 * <td>Escape the text as a ECMA Script (JavaScript, Jscript, ES6+, etc.) literal. This does not add surrounding quotes.<br>Example:
 * <code>&quot;Hello\n&quot;</code> becomes <code>\&quot;Hello\\n\&quot;</code></td>
 * </tr>
 * <tr>
 * <td>ECMA-ASCII</td>
 * <td>As ECMA, but also replaces all non-ASCII characters with numerical escapes</td>
 * </tr>
 * <tr>
 * <td>JAVA</td>
 * <td>Escape the text as a Java String literal. This does not add surrounding quotes.</td>
 * </tr>
 * <tr>
 * <td>JAVA-ASCII</td>
 * <td>As JAVA, but also replaces all non-ASCII characters with numerical escapes.</td>
 * </tr>
 * <tr>
 * <td>JSON</td>
 * <td>Escapes the text as a JSON String literal. This does not add surrounding quotes.</td>
 * </tr>
 * <tr>
 * <td>JSON-ASCII</td>
 * <td>As JSON, but also replaces all non-ASCII characters with numerical escapes.</td>
 * </tr>
 * <tr>
 * <td>URL</td>
 * <td>Escapes the text as a URL query parameter assuming UTF-8 encoding.</td>
 * </tr>
 * <tr>
 * <td>LOG</td>
 * <td>Escapes the text as a safe log message (see below)</td>
 * </tr>
 * </tbody></table>
 * <p>The difference between ECMA, Java and JSON encoding is subtle, and shown in the following table:</p>
 * <table>
 * <caption>Escape results</caption>
 * <thead>
 * <tr>
 * <th><strong>Character</strong></th>
 * <th><strong>ECMA</strong></th>
 * <th><strong>Java</strong></th>
 * <th><strong>JSON</strong></th>
 * </tr>
 * </thead>
 * <tbody><tr>
 * <td>Backspace (0x8)</td>
 * <td>\b</td>
 * <td>\b</td>
 * <td>\b</td>
 * </tr>
 * <tr>
 * <td>Tab (0x9)</td>
 * <td>\t</td>
 * <td>\t</td>
 * <td>\t</td>
 * </tr>
 * <tr>
 * <td>Line feed (0xA)</td>
 * <td>\n</td>
 * <td>\n</td>
 * <td>\n</td>
 * </tr>
 * <tr>
 * <td>Vertical tab (0xB)</td>
 * <td>\v</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>Form Feed (0xC)</td>
 * <td>\f</td>
 * <td>\f</td>
 * <td>\f</td>
 * </tr>
 * <tr>
 * <td>Carriage Return (0xD)</td>
 * <td>\r</td>
 * <td>\r</td>
 * <td>\r</td>
 * </tr>
 * <tr>
 * <td>Double Quote (0x22)</td>
 * <td>\&quot;</td>
 * <td>\&quot;</td>
 * <td>\&quot;</td>
 * </tr>
 * <tr>
 * <td>Single Quote (0x27)</td>
 * <td>\&#39;</td>
 * <td>\&#39;</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>Reverse Solidus (Back-slash) (0x5C)</td>
 * <td>\</td>
 * <td>\</td>
 * <td>\</td>
 * </tr>
 * <tr>
 * <td>Back-tick (0x60)</td>
 * <td>\`</td>
 * <td></td>
 * <td></td>
 * </tr>
 * </tbody></table>
 * <p>In ASCII mode, non-ASCII characters are replaced with numeric escapes.</p>
 * <p>If the character is within the Unicode Basic Multilingual Plane, the character is represented by a sequence like <code>\\uxxxx</code> where
 * <code>xxxx</code> represents a four digit
 * hexadecimal value.</p>
 * <p>For example the Euro symbol has Unicode code point <code>0x20AC</code> and so is represented as <code>\\u20ac</code>.</p>
 * <p>If a character is outside the Basic Multilingual Plane and the style is Java or JSON, it is represented by an escaped surrogate pair.  For example, the
 * clown-face emoji has Unicode code point <code>0x1F921</code> and is hence represented by <code>\ud83e\udd21</code>.</p>
 * <p>If a character is outside the Basic Multilingual Plane and the style is ECMA, it is represented by <code>\\u{xxxxx}</code>, where <code>xxxxx</code> is
 * the appropriate hexadecimal
 * value. For example, the clown-face emoji is represented as <code>\\u{1f921}</code>.</p>
 * <h3 id="log-escaping">&quot;Log&quot; escaping</h3>
 * <p>Log escaping is used to sanitise untrusted text with minimal alteration. If the text contains no new-lines, tabs, ISO control characters, surrogates,
 * private-use nor unassigned Unicode characters, then it is deemed safe and left unaltered.</p>
 * <p>If any form of new-line is found (CR, LF, CRLF, or LFCR) it is replaced with system line separator and all subsequent lines are prefix with a vertical
 * bar and some white space to make it clear that it is a continuation.</p>
 * <p>If a tab character is found, it is expanded to space characters with tab-stops at every 8th character position.</p>
 * <p>Orphaned surrogates are replaced with the Unicode replacement character. Valid surrogate pairs are left untouched.</p>
 * <p>All other control, private use, and unassigned characters are replaced with the Unicode replacement character.</p>
 */
package com.pippsford.stencil;
