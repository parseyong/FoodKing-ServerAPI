package com.example.foodking.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReplyEmotion is a Querydsl query type for ReplyEmotion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReplyEmotion extends EntityPathBase<ReplyEmotion> {

    private static final long serialVersionUID = 1712182995L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReplyEmotion replyEmotion = new QReplyEmotion("replyEmotion");

    public final EnumPath<EmotionType> emotionStatus = createEnum("emotionStatus", EmotionType.class);

    public final QReply reply;

    public final NumberPath<Long> replyEmotionId = createNumber("replyEmotionId", Long.class);

    public final QUser user;

    public QReplyEmotion(String variable) {
        this(ReplyEmotion.class, forVariable(variable), INITS);
    }

    public QReplyEmotion(Path<? extends ReplyEmotion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReplyEmotion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReplyEmotion(PathMetadata metadata, PathInits inits) {
        this(ReplyEmotion.class, metadata, inits);
    }

    public QReplyEmotion(Class<? extends ReplyEmotion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reply = inits.isInitialized("reply") ? new QReply(forProperty("reply"), inits.get("reply")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

