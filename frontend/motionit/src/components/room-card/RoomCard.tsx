import Image from "next/image";
import { RoomSummary, ChallengeStatus, RoomCta } from "../../type";

const cdnBaseUrl = process.env.NEXT_PUBLIC_CLOUD_FRONT_DOMAIN ?? "";

interface RoomCardProps {
    challenge: RoomSummary;
}

export default function RoomCard({challenge}: RoomCardProps) {

    return (
        <div>
            <article
              className="bg-white rounded-2xl shadow-sm border overflow-hidden flex flex-col"
            >
              <div className="relative aspect-[16/9]">
                <Image
                    src={`${cdnBaseUrl}/${challenge.roomImage}`}
                    alt={challenge.title}
                    fill
                    className="object-cover"
                />
              </div>

              <div className="p-4 flex flex-col gap-2">
                <h3 className="text-lg font-semibold">{challenge.title}</h3>
                <p className="text-sm text-neutral-600 truncate">{challenge.description}</p>

                <div className="mt-2 flex items-center justify-between text-xs text-neutral-500">
                    <span>
                        {challenge.current}/{challenge.capacity}명
                    </span>
                    <span> D-{challenge.dDay} </span>
                </div>

                <button
                    className={
                        challenge.status == ChallengeStatus.JOINABLE ?
                        "mt-3 w-full rounded-lg bg-emerald-600 text-white text-sm font-medium py-2 hover:bg-emerald-700 transition"
                        : "mt-3 w-full rounded-lg bg-gray-500 text-white text-sm font-medium py-2 hover:bg-gray-500 transition"
                    }
                    onClick={() => alert(`${challenge.title} ${RoomCta.JOIN}`)}
                >
                    {challenge.status == ChallengeStatus.JOINABLE ? RoomCta.JOIN : RoomCta.GET_IN}
                </button>
                </div>
            </article>
        </div>
    )
}