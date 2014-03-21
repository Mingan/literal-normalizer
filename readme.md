# Literal normalizer

This DPU allows normalization of string literals.

By specifying WHERE part of SPARQL query literals are selected for normalization. List of regular expression snippets is entered alongside the target value. All literals matching the regular expressions are modified and matching parts replaced by target string.