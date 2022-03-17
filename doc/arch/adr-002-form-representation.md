# ADR 2: Form representation and storage

## Context

Bugle forms needs a data model to support these uses:
- Users create forms with an many number of questions.
- Question forms can have a variety of field types—text, checkbox, radio buttons
  and dropdown boxes. It should be easy to add more response types later.
- Users provide an many responses for each question form. Each response maps to
  the structure and requirements of its question form.
- Sufficiently fast reads for retrieving form data and collating all answers in
  the responses.

![ER Diagram](https://user-images.githubusercontent.com/24277692/158169191-d3d9a6b0-9381-4ca3-b1da-0826fa3e8159.png)

The application currently uses PostgreSQL for storing user data.

With that in mind, some options include:
- Representing form questions and answers in JSON
  - using PostgreSQL's `json` type for the column having the form data.
  - using PostgreSQL's `text` type for the column having the form data.
  - using PostgreSQL's `jsonb` type as the column having the form data.
  - storing form data in a separate document store
- Representing and storing form questions and answers in relations
- Storing the form questions as HTML fragments, and storing the answers in a
  relation

## Tradeoffs

### Using relations

#### The model

Assuming we only accept text answers, the relational model would look like this:

![Relational-only model with only text](https://user-images.githubusercontent.com/24277692/158169513-45cfc41b-b70b-441d-bc6d-a4a290bbea0d.png)

#### Benefits

Good performance compared to using JSON. More compact storage. Easy indexing opportunities.

#### Drawbacks

Adding answer types would mean adding an extra relation to support the answer type, and would require changes on the database model as well as business logic.

For example, adding a checkbox answer type on top of the above relational model would result in:
![Relational-only model with text and checkbox](https://user-images.githubusercontent.com/24277692/158170901-dcb0835d-8cfe-42f1-9143-c48befc5890d.png)

The schema will require changes for every type of answer that is added.

Another drawback is that this model will not support a defined order of questions. That would require embedding some sequence information into the data model, which is more complex than with full-JSON form representations.

#### Adding ordering to relational models

In order to support a defined order of questions that adapt based on users shuffling questions or deleting them in between the form, we can add an attribute having PostgreSQL array type, which lists the order of question IDs. This makes it possible to cheaply and easily reshuffle the order of questions.

The main drawback in this approach is that there is no enforcement of the integrity of question IDs in the array attribute. This means that if we are not careful, we might delete an ID while still having a reference to it in the array attribute.

There are other approaches to ensure ordering as well:
- Have an `order` column, which is a serial index that determines the order. If we delete something in between, we have to update `order` for all the rows ahead of it. Same issue with adding questions in between.
- Make it behave like a linked list, ie, a `next-question` attribute instead of `order`. This makes deletion and insertion in between touch less rows, since we only have to change the links. The issue with that is displaying the questions in order means that we have to follow the links, which would be tedious, requiring _N_ queries to show _N_ questions.

### Using JSON

#### Implementation details

The illustration below demonstrates how we will model the form data.

![Data model](https://user-images.githubusercontent.com/24277692/156551390-af11e56a-f8d6-4d7f-a55b-b8d4e511104c.png)

We still use relations for storing form and response IDs, along with their
metadata—which would be information like author, and possibly in the future,
creation and expiry timestamps. The form structure will be stored in a `jsonb`
column.

JSON arrays represent a forms and responses. Each item in an array will be an
object containing a unique (to the form) question ID along with a type field and
its associated metadata (respondent, versioning, etc.).

Here's an example of a form with two questions:
```json
[
  {
    "id": "a283fe",
    "type": "text",
    "question": "What did you have for oota?"
  },
  {
    "id": "ca92e3",
    "type": "single-choice",
    "question": "Pick your favourite language.",
    "choice": ["Clojure", "C++", "Python", "Gujarati"],
    "mandatory": true
  }
]
```

The application converts these JSON representation of the questions to HTML
forms when presented to a responder.

Responses are collected and converted to a JSON representation as well. A sample
response to this form might look like:
```json
[
  {
    "id": "a283fe",
    "type": "text",
    "answer": "Undhiyu and Rotla"
  },
  {
    "id": "ca92e3",
    "type": "single-choice",
    "answer": "Gujarati"
  }
]
```

Note that the IDs in responses correspond to the id of the question they answer.
This simplifies matching a question to an answer.

#### Benefits

- Easier to understand model, compared to using relations.
- A well understood, portable format—no need for special application logic to
  examine form data.
- Ordering is defined if we store the complete form representation and response representation as a JSON array (details in decision section).

#### Drawbacks

- Performance is worse than using relations—fields in general are not as
  amenable to indexing.

#### JSON, but only for storing answer types

The model for this case would use relations as much as possible for rigidly defined relationships and only leave JSON columns for type-flexible questions and answers. An example of this would look like:

![Hybrid relational and JSON model](https://user-images.githubusercontent.com/24277692/158172033-7244bd75-9eec-4b0c-9b1a-1c8adc3e8555.png)

This would only limit the schemaless-ness to where it is needed. It still won't solve the ordering problem. Refer to discussion above for how to add ordering in a relational-only model.

#### Differences between the JSON-compatible Postgres column types

- `text`: Stores JSON as plaintext—no validation or indexing possible.
- `json`: Validates inserted JSON, but objects are still stored as plaintext,
  and fields are not directly indexable.
- `jsonb`: Stored in a binary format—provides validation and better indexing
  opportunities. Provides much faster read performance at the cost of slightly
  higher storage usage and slower write times.
  
### Using a separate document store

#### Benefits

- Modern document databases encode their data in JSON, and as such share the
  same benefits as using a JSON column for form data.

#### Drawbacks

- Adds another heavyweight dependency to the project, only to store forms.

### Storing form questions as HTML directly

#### Benefits

- Minimal or no work needed for the web application to translate between the
  form data representation to a user-facing view.

#### Drawbacks

- Editing the form would require programmatically editing HTML fragments, which
  is tedious.
- Presentation and representation of form data is coupled.
- We still need to handle responses in a separate format, so data
  representations become uneven.

## Decision

We will use a fully relational model for modeling our forms. At first, we will only support short text responses to questions. When we want to add more question types, we will use JSON attributes only for storing question  and answer specific details like option values. A migration will be needed for the same.

## Status

Accepted.

## Consequences

Using our relations-heavy model will provide strong enforcement of the entity relationships, barring the ordering array, where we cannot ensure that it has valid references. 

We can expect good performance and indexing opportunities.

There will also be a database migration operation that we need to carry out when we switch to more answer types. We will not guarantee the shape of the question and answer related data (such as options selected) since it will be in JSON.

This will also affect the viability of various ordering implementations. With a JSON representation, ordering is implicit, and same as the order of the array. Picking a relational model will need more deliberate ordering implementations.
