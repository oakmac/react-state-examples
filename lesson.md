# re-frame + schema Lesson

- What is a re-frame interceptor?
  - {:id :something
     :before (fn [context] ...)
     :after (fn [context] ...)}
- re-frame path
  - https://day8.github.io/re-frame/api-re-frame.core/#path
  - https://github.com/day8/re-frame/blob/master/examples/todomvc/src/todomvc/events.cljs
- goog.DEBUG
  - run code in development; hide from production via compiler flag
- Malli https://github.com/metosin/malli
  - describe the structure of your data declaratively
  - get validation functions (ie: predicates), helpful errors, and confidence!
