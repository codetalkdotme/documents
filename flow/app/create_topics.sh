bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 5 --topic flow-quest-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 5 --topic flow-user-signup
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 5 --topic flow-user-update
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 8 --topic flow-post-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 12 --topic flow-post-comment-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 8 --topic flow-quest-reply-create
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 3 --partitions 12 --topic flow-quest-comment-create

