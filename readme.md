# Literal normalizer

DPU for [UnifiedViews](https://github.com/UnifiedViews/Core)/[ODCS](https://github.com/mff-uk/ODCS) for normalizing string literals.

## Usage

This DPU modifies triples with literal objects. It replaces list of values and normalizes them to a given target value. Filtering by language tag is enabled and tags are used for annotating replacement value as well.

1. Specify condition part of SPARQL WHERE clause, you must refer to the literal being updated as `?o`.
2. Specify triple pattern which should be modified. It will be part of the previous field, `?o` should be in a position of object.
3. List values which should be replaced, one on a line.
4. Enter normalized value.
5. Enter language code for filtering. Default is empty which will match only string literals without language tag attached.
6. Run pipeline.

### Simple mode

Values are matched by `=` in SPARQL in simple mode, therefore only exact matches are converted. Be aware of uppercase/lowercase variants.

### Regular expression mode

In regular expression mode the listed values are treated as regular expression snippets wrapped in `^(.*)` and `(.*)$`. Replacement is constructed as `$1$2`. If your matched literals are more complex you might want to consider starting and ending the string with `\b` for word boundary or `\s` for whitespace to prevent false positives. When using reserved characters like `.` don't forget to escape them by two backslashes, eg. `\\.`.

## Behind the scenes

As a first step DPU copies input triples to output and then runs the constructed SPARQL query on it. With correctly entered configuration number of triples on the input matches the number on the output. It is possible to delete triples you want to keep by incorrectly constructed condition + triple pattern, test it first!

The generated query uses given condition plus `isLiteral(?o)` to filter triples for processing. In fact, this condition is generated for every value in the list and then `UNION`ed together. Each graph pattern has filter for one item in the list. Simple comparison is used in simple mode, filtering with regexp and binding of replacement is used in regexp mode.

Variable `?o` is special, because it is used to bind conditions for selection and patterns for removing old and inserting new triple.