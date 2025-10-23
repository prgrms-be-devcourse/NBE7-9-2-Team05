import { APP_NAME, CHALLENGE_ROOM, SETTINGS } from "../../constants";

export default function MainHeader() {
    return (
        <header className="sticky top-0 z-20 bg-white border-b">
            <div className="mx-auto max-w-6xl px-4 h-16 flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <span className="text-2xl font-extrabold tracking-tight">{APP_NAME}</span>
                    <span className="ml-2 text-sm text-neutral-500">{CHALLENGE_ROOM}</span>
                </div>

                <div className="flex items-center gap-2">
                    <button
                        className="h-8 w-8 rounded-full flex items-center justify-center leading-none text-2xl"
                        aria-label="More"
                    >
                        {SETTINGS}
                    </button>
                </div>
            </div>
      </header>
    )
}