#!/bin/sh

PORT=3000

get_container_id() {
	cat ".container"
}

is_running() {
	if [ -f ".container" ]; then
		docker inspect --format="{{ .State.Running }}" `get_container_id` 2> /dev/null | grep true > /dev/null 2>&1
	else
		return 1
	fi
}

case "$1" in
    start)
    if is_running; then
        echo "Already started"
    else
        echo "Starting server..."
        docker run -d -v $(pwd):/root -w /root -p $PORT:3000 tokenshift/sprint-poker > .container

        if ! is_running; then
            echo "Failed to start!"
            exit 1
        fi
    fi
    ;;

    stop)
    if is_running; then
        echo -n "Stopping server..."
        docker stop `get_container_id`

        for i in {1..10}
        do
            if ! is_running; then
                break
            fi

            echo -n "."
            sleep 1
        done

        if is_running; then
            echo "Not stopped; may still be shutting down or shutdown may have failed"
            exit 1
        else
            echo "Stopped"
            if [ -f ".container" ]; then
                rm ".container"
            fi
        fi
    else
        echo "Not running"
    fi
    ;;

    restart)
    $0 stop
    if is_running; then
        echo "Unable to stop, will not attempt to start"
        exit 1
    fi
    $0 start
    ;;
    status)
    if is_running; then
        echo "Running"
    else
        echo "Stopped"
        exit 1
    fi
    ;;
    *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
    ;;
esac

exit 0
