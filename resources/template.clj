(ns check.{{class|safe}}
  (:require [clojure.test :refer :all]))

(defn {{function|safe}}
  [{% for p in parameters %}{% if not forloop.first %} {% endif %}{{p|safe}}{% endfor %}]
  )
{% for io in ios %}
(deftest example-{{forloop.counter0|safe}}
  (is (= ({{function|safe}}{% for i in io %}{% if forloop.last %}) {{i|safe}})))
{% else %} {{i|safe}}{% endif %}{% endfor %}{% endfor %}