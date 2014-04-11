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

Values are matched via `VALUES` in simple mode, therefore only exact matches are converted. Matching is done by `FILTER` for case insensitive mode.

### Regular expression mode

In regular expression mode the listed values are treated as regular expression snippets which are then joined to one regular expression with `|` in between. If your matched literals are more complex you might want to consider starting and ending the string with `\b` for word boundary or `\s` for whitespace to prevent false positives. When using reserved characters like `.` don't forget to escape them by two backslashes, eg. `\\.`.

## Behind the scenes

As a first step DPU copies input triples to output and then runs the constructed SPARQL query on it. With correctly entered configuration number of triples on the input matches the number on the output. It is possible to delete triples you want to keep by incorrectly constructed condition + triple pattern, test it first!

The generated query uses given condition plus `isLiteral(?o)` when necessary (regular expression and simple case-insensitive mode) to filter triples for processing. Similarly language filtering is applied. In simple case-sensitive mode exact matching is used via `VALUES` construct. Language tag is attached from the setting. In case you have different settings for different languages, you have to use multiple instances.

Variable `?o` is special, because it is used to bind conditions for selection and patterns for removing old and inserting new triple. Variable `?o_temp` is used in simple case-insensitive mode for filtering. In other modes, it can be used freely.