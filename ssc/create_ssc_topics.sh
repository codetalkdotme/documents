bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 5 --topic ssc-quest-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 5 --topic ssc-user-signup
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 5 --topic ssc-user-update
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 8 --topic ssc-post-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 12 --topic ssc-post-comment-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 8 --topic ssc-quest-reply-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 12 --topic ssc-quest-comment-create

