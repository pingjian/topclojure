(ns check
  (:require [clojure.test :refer :all]))

(defn {{function}}
  [{% for p in parameters %}{% if not forloop.first %} {% endif %}{{p}}{% endfor %}]
  )
{% for io in ios %}
(deftest test-{{function}}
  (is (= ({{function}}{% for i in io %}{% if forloop.last %}) {{i}})))
{% else %} {{i}}{% endif %}{% endfor %}{% endfor %}