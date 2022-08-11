# The Stencil Template System

The Stencil Template System produces text based documents by combining input data with a template.

A template consists of static text, directives, and values.

Static text is included in the output from the template and never changes.

Directives affect how the template is processed. Directives are indicated by the use of square brackets: `[` and `]`.

Values present the input data as text in the output. Values are indicated by the use of curly brackets: `{` and `}`.

## Example

```
<b>Description</b>
Colour: {colour}
Weight: [if isWeightKnown]{weight}[else]<i>Unknown</i>[end]

[loop properties]
  {key} : {value}
[else]
  No properties present
[end]
```

## Escape Processing

Every value is passed through a post-processor to "escape" special characters in the output. For example, HTML output requires the 
correct characters like `<` and `>` be replaced by the correct character entity representations.

The built-in escape processors are:

### NO-ESCAPE
Does not change anything.

### HTML-STRICT
All characters in the input that need to be replaced by character entities in HTML are so replaced.

### HTML-SAFE

### ECMA

### JAVA

### JSON

### URL

### LOG
